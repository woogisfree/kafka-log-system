package com.example.consumerworker.integration;

import com.example.consumerworker.domain.DlqMessage;
import com.example.consumerworker.domain.UserLog;
import com.example.consumerworker.dto.UserLogMessage;
import com.example.consumerworker.repository.DlqMessageRepository;
import com.example.consumerworker.repository.UserLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

@EnableKafka
@EmbeddedKafka(partitions = 1, topics = "user-activity-log")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private DlqMessageRepository dlqMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        userLogRepository.deleteAll();
    }

    @DisplayName("Kafka를 통해 전송된 로그 메시지가 DB에 저장되는지 검증한다.")
    @Test
    void givenUserLogMessage_whenProduced_thenConsumedAndSaved() throws Exception {
        //given
        UserLogMessage request = new UserLogMessage("jin", "click", LocalDateTime.now());
        String message = objectMapper.writeValueAsString(request);

        //when
        kafkaTemplate.send("user-activity-log", message);

        //then
        Thread.sleep(2000); // Consumer가 저장할 시간 살짝 대기

        assertThat(userLogRepository.findAll())
                .extracting(UserLog::getUserId, UserLog::getAction)
                .containsExactly(tuple("jin", "click"));
    }

    @DisplayName("Kafka 소비 실패 시 DLQ로 전송되고 DB에 저장되는지 검증한다")
    @Test
    void givenInvalidUserLogMessage_whenProduced_thenSavedToDlqTable() throws Exception {
        // given
        String invalidJson = "{\"userId\":\"jin\"}"; // 'action', 'timestamp' 누락

        // when
        kafkaTemplate.send("user-activity-log", invalidJson);

        // then
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    List<DlqMessage> dlqMessages = dlqMessageRepository.findAll();
                    assertThat(dlqMessages).hasSize(1);

                    DlqMessage dlqMessage = dlqMessages.get(0);

                    // rawMessage에 원래 보낸 invalidJson 포함
                    assertThat(dlqMessage.getRawMessage()).contains("jin");

                    // errorMessage에 Validation 실패 관련 메시지 포함
                    assertThat(dlqMessage.getErrorMessage())
                            .contains("Listener method")
                            .contains("consume");
                });
    }

    @DisplayName("Kafka로 3개의 메시지를 전송했을 때 모두 DB에 저장되는지 검증한다")
    @Test
    void givenMultipleUserLogMessages_whenProduced_thenAllConsumedAndSaved() throws Exception {
        // given
        List<UserLogMessage> messages = List.of(
                new UserLogMessage("user1", "click", LocalDateTime.now()),
                new UserLogMessage("user2", "view", LocalDateTime.now()),
                new UserLogMessage("user3", "purchase", LocalDateTime.now())
        );

        for (UserLogMessage msg : messages) {
            kafkaTemplate.send("user-activity-log", objectMapper.writeValueAsString(msg));
        }

        // when & then
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    List<UserLog> userLogs = userLogRepository.findAll();
                    assertThat(userLogs).hasSize(messages.size());

                    assertThat(userLogs)
                            .extracting(UserLog::getUserId, UserLog::getAction)
                            .containsExactlyInAnyOrder(
                                    tuple("user1", "click"),
                                    tuple("user2", "view"),
                                    tuple("user3", "purchase")
                            );
                });
    }

    @DisplayName("Kafka로 100개의 메시지를 전송했을 때 모두 DB에 저장되는지 검증한다")
    @Test
    void givenManyUserLogMessages_whenProduced_thenAllConsumedAndSaved() throws Exception {
        // given
        int messageCount = 100;
        for (int i = 0; i < messageCount; i++) {
            UserLogMessage msg = new UserLogMessage("user" + i, "click", LocalDateTime.now());
            kafkaTemplate.send("user-activity-log", objectMapper.writeValueAsString(msg)).get(); // 동기
        }

        // when & then
        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<UserLog> userLogs = userLogRepository.findAll();
                    assertThat(userLogs).hasSize(messageCount);
                });
    }
}
