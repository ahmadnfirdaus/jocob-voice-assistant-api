package com.jocob.voice.assistant.controller;

import com.jocob.voice.assistant.service.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    private final SpeechToTextService speechService;

    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribe(@RequestParam("file") MultipartFile file) throws Exception {
        File temp = File.createTempFile("upload", ".ogg");
        file.transferTo(temp);
        return ResponseEntity.ok(speechService.transcribeOgg(temp));
    }
}
