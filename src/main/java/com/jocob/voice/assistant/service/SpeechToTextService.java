package com.jocob.voice.assistant.service;

import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

@Service
public class SpeechToTextService {

    public String transcribeOgg(File oggFile) throws Exception {
        // Load Vosk model
        try (Model model = new Model("src/main/resources/model");
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

                return recognizer.getFinalResult();
            }
        }
    }
}
