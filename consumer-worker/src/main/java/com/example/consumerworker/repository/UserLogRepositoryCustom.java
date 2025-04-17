package com.example.consumerworker.repository;

import com.example.consumerworker.domain.UserLog;

import java.time.LocalDateTime;
import java.util.List;

public interface UserLogRepositoryCustom {
    List<UserLog> search(String userId, String action, LocalDateTime from, LocalDateTime to);
}
