package com.example.producerapi.controller;

import com.example.producerapi.dto.UserLogRequest;
import com.example.producerapi.service.KafkaProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping
    public ResponseEntity<Void> sendLog(@Valid @RequestBody UserLogRequest request) {
        kafkaProducerService.sendLog(request);
        return ResponseEntity.ok().build();
    }
}
