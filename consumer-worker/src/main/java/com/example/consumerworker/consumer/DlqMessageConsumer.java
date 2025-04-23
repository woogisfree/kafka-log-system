package com.example.consumerworker.consumer;

import com.example.consumerworker.domain.DlqMessage;
import com.example.consumerworker.repository.DlqMessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlqMessageConsumer {

    private final DlqMessageRepository dlqMessageRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-activity-log-dlq", groupId = "dlq-consumer-group")
    public void consume(ConsumerRecord<String, String> record) {
        String messageValue = record.value();
        try {
            Map<String, String> dlqContent = objectMapper.readValue(messageValue, new TypeReference<>() {});
            String rawMessage = dlqContent.get("rawMessage");
            String errorMessage = dlqContent.get("errorMessage");

            DlqMessage entity = DlqMessage.builder()
                    .rawMessage(rawMessage)
                    .errorMessage(errorMessage)
                    .build();
            dlqMessageRepository.save(entity);
            log.info("Saved DLQ message={}", rawMessage);
        } catch (JsonProcessingException e) {
            log.error("❌ Failed to parse DLQ message", e);
        }
    }
}
