package com.example.producerapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserLogRequest {
    private String userId;
    private String action;
    private LocalDateTime timestamp;
}
