package com.jocob.voice.assistant.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jocob.voice.assistant.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@Service
public class SpeechToTextService {

    private final ResourceExtractor resourceExtractor;

    public String transcribeOgg(File oggFile) throws IOException, UnsupportedAudioFileException, URISyntaxException {
        File modelDir = resourceExtractor.extractResourceDir("model");

        try (Model model = new Model(modelDir.getAbsolutePath());
             AudioInputStream ais = AudioSystem.getAudioInputStream(oggFile)) {

            AudioFormat baseFormat = ais.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);

            try (AudioInputStream din = AudioSystem.getAudioInputStream(targetFormat, ais);
                 Recognizer recognizer = new Recognizer(model, 16000)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = din.read(buffer)) >= 0) {
                    recognizer.acceptWaveForm(buffer, bytesRead);
                }

                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                ResponseDTO responseDTO = gson.fromJson(recognizer.getFinalResult(), ResponseDTO.class);
                return responseDTO.getText();
            }
        }
    }
}
