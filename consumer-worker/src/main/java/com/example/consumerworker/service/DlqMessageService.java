package com.example.consumerworker.service;

import com.example.consumerworker.dto.DlqMessageResponse;
import com.example.consumerworker.repository.DlqMessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DlqMessageService {

    private final DlqMessageRepository dlqMessageRepository;
    private final ObjectMapper objectMapper;

    public List<DlqMessageResponse> getDlqMessages() {
        return dlqMessageRepository.findAll().stream()
                .map(msg -> {

                    Object rawMessageObj;
                    try {
                        rawMessageObj = objectMapper.readValue(msg.getRawMessage(), Object.class);
                        log.info("rawMessageObj={}", rawMessageObj);
                    } catch (JsonProcessingException e) {
                        rawMessageObj = Map.of("parseError", "Invalid JSON");
                    }
                    return new DlqMessageResponse(
                            msg.getId(),
                            rawMessageObj,
                            msg.getErrorMessage(),
                            msg.getCreatedAt());
                }).toList();
    }
}
