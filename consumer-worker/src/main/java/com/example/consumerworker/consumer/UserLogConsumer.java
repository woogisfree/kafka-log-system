package com.example.consumerworker.consumer;

import com.example.consumerworker.domain.UserLog;
import com.example.consumerworker.dto.UserLogMessage;
import com.example.consumerworker.repository.UserLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLogConsumer {

    private final UserLogRepository userLogRepository;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @KafkaListener(
            topics = "user-activity-log",
            groupId = "log-consumer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String json = record.value();
        UserLogMessage message = objectMapper.readValue(json, UserLogMessage.class);
        Set<ConstraintViolation<UserLogMessage>> violations = validator.validate(message);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + violations);
        }

        UserLog entity = UserLog.builder()
                .userId(message.getUserId())
                .action(message.getAction())
                .timestamp(message.getTimestamp())
                .build();
        userLogRepository.save(entity);
        log.info("✅ Saved log: {}", json);
    }
}
