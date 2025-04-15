package com.example.producer_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLogRequest {
    private String userId;
    private String action;
    private String timestamp; // ISO 8601 형식
}
