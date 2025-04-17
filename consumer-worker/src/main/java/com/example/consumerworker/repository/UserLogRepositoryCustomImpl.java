package com.example.consumerworker.repository;

import com.example.consumerworker.domain.QUserLog;
import com.example.consumerworker.domain.UserLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class UserLogRepositoryCustomImpl implements UserLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserLog> search(String userId, String action, LocalDateTime from, LocalDateTime to) {
        QUserLog userLog = QUserLog.userLog;

        return queryFactory
                .selectFrom(userLog)
                .where(
                        StringUtils.hasText(userId) ? userLog.userId.eq(userId) : null,
                        StringUtils.hasText(action) ? userLog.action.eq(action) : null,
                        from != null ? userLog.timestamp.goe(from) : null,
                        to != null ? userLog.timestamp.loe(to) : null
                )
                .fetch();
    }
}
