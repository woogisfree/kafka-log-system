package com.example.consumerworker.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLogMessage {
    private String userId;
    private String action;
    private String timestamp;
}
