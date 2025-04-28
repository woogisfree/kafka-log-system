package com.example.consumerworker.consumer;

import com.example.consumerworker.domain.DlqMessage;
import com.example.consumerworker.repository.DlqMessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DlqMessageConsumerTest {

    @Mock
    private DlqMessageRepository dlqMessageRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DlqMessageConsumer dlqMessageConsumer;
    
    @DisplayName("정상적인 DLQ 메시지를 수신하면 DB에 저장한다")
    @Test
    void consume_shouldSaveDlqMessage_whenMessageIsValid() throws Exception {
        //given
        Map<String, String> dlqContent = new HashMap<>();
        dlqContent.put("rawMessage", "{\"userId\":\"jin\",\"action\":\"click\"}");
        dlqContent.put("errorMessage", "Validation failed");

        String json = "{\"rawMessage\":\"{\\\"userId\\\":\\\"jin\\\",\\\"action\\\":\\\"click\\\"}\",\"errorMessage\":\"Validation failed\"}";
        when(objectMapper.readValue(eq(json), any(TypeReference.class))).thenReturn(dlqContent);

        ConsumerRecord<String, String> record = new ConsumerRecord<>("user-activity-log-dlq", 0, 0, null, json);

        //when
        dlqMessageConsumer.consume(record);
        
        //then
        ArgumentCaptor<DlqMessage> captor = ArgumentCaptor.forClass(DlqMessage.class);
        verify(dlqMessageRepository, times(1)).save(captor.capture());

        DlqMessage savedEntity = captor.getValue();
        assertThat(savedEntity.getRawMessage()).isEqualTo("{\"userId\":\"jin\",\"action\":\"click\"}");
        assertThat(savedEntity.getErrorMessage()).isEqualTo("Validation failed");
    }
    
    @DisplayName("JSON 파싱에 실패하면 DB에 저장하지 않는다")
    @Test
    void consume_shouldNotSaveDlqMessage_whenJsonProcessingFails() throws Exception {
        //given
        String badJson = "{invalid json}";
        when(objectMapper.readValue(eq(badJson), any(TypeReference.class))).thenThrow(JsonProcessingException.class);

        ConsumerRecord<String, String> record = new ConsumerRecord<>("user-activity-log-dlq", 0, 0, null, badJson);

        //when
        dlqMessageConsumer.consume(record);
        
        //then
        verify(dlqMessageRepository, never()).save(any());
    }
}