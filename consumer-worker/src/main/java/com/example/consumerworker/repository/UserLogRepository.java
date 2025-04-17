package com.example.consumerworker.repository;

import com.example.consumerworker.domain.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLog, Long>, UserLogRepositoryCustom {

}
