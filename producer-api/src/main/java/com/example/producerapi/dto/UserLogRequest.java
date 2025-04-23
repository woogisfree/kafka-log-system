package com.example.producerapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogRequest {

    @NotNull
    private String userId;

    @NotNull
    private String action;
    private LocalDateTime timestamp;
}
