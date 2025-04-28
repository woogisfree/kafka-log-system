package com.example.consumerworker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogMessage {

    @NotNull
    private String userId;

    @NotNull
    private String action;

    @NotNull
    private LocalDateTime timestamp;
}
