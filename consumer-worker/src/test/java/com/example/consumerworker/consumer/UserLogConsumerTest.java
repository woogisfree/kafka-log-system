package com.example.consumerworker.consumer;

import com.example.consumerworker.domain.UserLog;
import com.example.consumerworker.dto.UserLogMessage;
import com.example.consumerworker.repository.UserLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLogConsumerTest {

    @Mock
    private UserLogRepository userLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserLogConsumer userLogConsumer;

    @DisplayName("정상적인 메시지를 수신하면 DB에 저장한다")
    @Test
    void consume_shouldSaveUserLog_whenMessageIsValid() throws Exception {
        //given
        String json = "{\"userId\":\"jin\",\"action\":\"click\",\"timestamp\":\"2025-04-28T10:00:00\"}";
        UserLogMessage message = new UserLogMessage("jin", "click", LocalDateTime.of(2025, 4, 28, 10, 00));

        when(objectMapper.readValue(json, UserLogMessage.class)).thenReturn(message);
        when(validator.validate(message)).thenReturn(Set.of());

        ConsumerRecord<String, String> record = new ConsumerRecord<>("user-activity-log", 0, 0, null, json);

        //when
        userLogConsumer.consume(record);

        //then
        ArgumentCaptor<UserLog> captor = ArgumentCaptor.forClass(UserLog.class);
        verify(userLogRepository, times(1)).save(captor.capture());

        UserLog savedEntity = captor.getValue();
        assertThat(savedEntity.getUserId()).isEqualTo("jin");
        assertThat(savedEntity.getAction()).isEqualTo("click");
        assertThat(savedEntity.getTimestamp()).isEqualTo(LocalDateTime.of(2025, 4, 28, 10, 0));
    }

    @DisplayName("메시지 검증에 실패하면 예외를 던진다")
    @Test
    void consume_shouldThrowException_whenValidationFails() throws Exception {
        //given
        String json = "{\"userId\":\"jin\",\"action\":\"\",\"timestamp\":\"2025-04-28T10:00:00\"}";
        UserLogMessage message = new UserLogMessage("jin", "", LocalDateTime.of(2025, 4, 28, 10, 0));

        ConstraintViolation<UserLogMessage> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<UserLogMessage>> violations = Set.of(violation);

        when(objectMapper.readValue(json, UserLogMessage.class)).thenReturn(message);
        when(validator.validate(message)).thenReturn(violations);

        ConsumerRecord<String, String> record = new ConsumerRecord<>("user-activity-log", 0, 0, null, json);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> userLogConsumer.consume(record));
        verify(userLogRepository, never()).save(any());
    }
}