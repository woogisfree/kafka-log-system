package com.example.producerapi.controller;

import com.example.producerapi.dto.UserLogRequest;
import com.example.producerapi.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("로그 전송 API는 성공적으로 200 OK를 반환한다")
    @Test
    void sendLog_shouldReturn200_whenRequestValid() throws Exception {
        //given
        UserLogRequest request = new UserLogRequest("jin", "click", LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(request);
        
        //when & then
        mockMvc.perform(post("/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        verify(kafkaProducerService).sendLog(any(UserLogRequest.class));
    }
    
    @DisplayName("잘못된 요청값이 들어오면 400 Bad Request를 반환한다")
    @Test
    void sendLog_shouldReturn400_whenInvalidJson() throws Exception {
        //given
        String badRequestJson = "{\"userId\":\"jin\", \"action\":\"click\", \"timestamp\":\"hello\"}";
        
        //when & then
        mockMvc.perform(post("/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badRequestJson))
                .andExpect(status().isBadRequest());

        verify(kafkaProducerService, never()).sendLog(any());
    }
}