package com.example.producer_api.controller;

import com.example.producer_api.dto.UserLogRequest;
import com.example.producer_api.service.KafkaProducerService;
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
    public ResponseEntity<Void> sendLog(@RequestBody UserLogRequest request) {
        kafkaProducerService.sendLog(request);
        return ResponseEntity.ok().build();
    }
}
