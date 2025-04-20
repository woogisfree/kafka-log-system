package com.example.consumerworker.consumer;

import com.example.consumerworker.domain.DlqMessage;
import com.example.consumerworker.repository.DlqMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlqMessageConsumer {

    private final DlqMessageRepository dlqMessageRepository;

    @KafkaListener(topics = "user-activity-log-dlq", groupId = "dlq-consumer-group")
    public void consume(ConsumerRecord<String, String> record) {
        String rawMessage = record.value();

        DlqMessage entity = DlqMessage.builder()
                .rawMessage(rawMessage)
                .errorMessage("Validation failed or deserialization error")
                .build();

        dlqMessageRepository.save(entity);
        log.info("Saved DLQ message={}", rawMessage);
    }
}
