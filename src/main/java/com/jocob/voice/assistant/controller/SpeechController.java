package com.jocob.voice.assistant.controller;

import com.jocob.voice.assistant.service.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> transcribe(@RequestParam("file") MultipartFile file) throws Exception {
        File temp = File.createTempFile("upload", "-".concat(file.getOriginalFilename()));
        file.transferTo(temp);
        return ResponseEntity.ok(speechService.transcribeOgg(temp));
    }
}
