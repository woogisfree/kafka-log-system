package com.example.consumerworker.controller;

import com.example.consumerworker.dto.DlqMessageResponse;
import com.example.consumerworker.service.DlqMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dlq-logs")
public class DlqMessageController {

    private final DlqMessageService dlqMessageService;

    @GetMapping
    public ResponseEntity<List<DlqMessageResponse>> getDlqMessages() {
        return ResponseEntity.ok(dlqMessageService.getDlqMessages());
    }
}
