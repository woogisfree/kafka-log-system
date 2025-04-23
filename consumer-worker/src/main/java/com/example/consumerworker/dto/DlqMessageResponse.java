package com.example.consumerworker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DlqMessageResponse {
    private Long id;
    private Object rawMessage;
    private String errorMessage;
    private LocalDateTime createdAt;
}
