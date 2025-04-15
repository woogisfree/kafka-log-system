package com.example.consumerworker.consumer;

import com.example.consumerworker.domain.UserLog;
import com.example.consumerworker.dto.UserLogMessage;
import com.example.consumerworker.repository.UserLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLogConsumer {

    private final UserLogRepository userLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-activity-log", groupId = "log-consumer-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String json = record.value();
            UserLogMessage message = objectMapper.readValue(json, UserLogMessage.class);

            UserLog entity = UserLog.builder()
                    .userId(message.getUserId())
                    .action(message.getAction())
                    .timestamp(message.getTimestamp())
                    .build();
            userLogRepository.save(entity);
            log.info("✅ Saved log: {}", json);
        } catch (Exception e) {
            log.error("❌ Failed to consume message: {}", record.value(), e);
        }
    }
}
