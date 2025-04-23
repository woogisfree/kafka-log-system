package com.example.producerapi.service;

import com.example.producerapi.dto.UserLogRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @DisplayName("성공적으로 직렬화되면 Kafka 메시지를 전송한다")
    @Test
    void sendLog_shouldSendKafkaMessage_whenSerializationSucceeds() throws Exception {
        //given
        UserLogRequest request = new UserLogRequest("jin", "click", LocalDateTime.now());
        String message = "{\"userId\":\"jin\",\"action\":\"click\",\"timestamp\":\"...\"}";
        given(objectMapper.writeValueAsString(request)).willReturn(message);

        //when
        kafkaProducerService.sendLog(request);

        //then
        verify(kafkaTemplate, times(1)).send("user-activity-log", message);
    }

    @DisplayName("직렬화에 실패하면 에러 로그를 출력한다")
    @Test
    void sendLog_shouldLogError_whenSerializationFails() throws Exception {
        //given
        UserLogRequest request = new UserLogRequest("jin", "click", LocalDateTime.now());
        given(objectMapper.writeValueAsString(any())).willThrow(JsonProcessingException.class);

        //when
        kafkaProducerService.sendLog(request);

        //then
        verify(kafkaTemplate, never()).send(any(), any());
    }
}