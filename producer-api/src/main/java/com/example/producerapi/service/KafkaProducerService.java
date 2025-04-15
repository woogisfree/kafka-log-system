package com.example.producerapi.service;

import com.example.producerapi.dto.UserLogRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendLog(UserLogRequest logRequest) {
        try {
            String message = objectMapper.writeValueAsString(logRequest);
            kafkaTemplate.send("user-activity-log", message);
            log.info("Produced Message={}", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message", e);
        }
    }
}
