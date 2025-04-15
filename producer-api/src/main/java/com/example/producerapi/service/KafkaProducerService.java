package com.example.producerapi.service;

import com.example.producerapi.dto.UserLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendLog(UserLogRequest logRequest) {
        String message = String.format(
                "{\"userId\":\"%s\", \"action\":\"%s\", \"timestamp\":\"%s\"}",
                logRequest.getUserId(), logRequest.getAction(), logRequest.getTimestamp());

        kafkaTemplate.send("user-activity-log", message);
    }
}
