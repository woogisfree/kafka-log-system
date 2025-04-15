package com.example.consumerworker.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserLogMessage {
    private String userId;
    private String action;
    private LocalDateTime timestamp;
}
