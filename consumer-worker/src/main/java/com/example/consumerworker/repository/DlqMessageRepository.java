package com.example.consumerworker.repository;

import com.example.consumerworker.domain.DlqMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DlqMessageRepository extends JpaRepository<DlqMessage, Long> {
}
