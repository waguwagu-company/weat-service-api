package com.waguwagu.weat.domain.group.model.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Slf4j
public class QuerydslTest {
    @PersistenceContext
    EntityManager em;

    @Test
    void querydslTest() {
        // given
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QGroup qGroup = QGroup.group;

        // when
        Group foundGroup = queryFactory
                .selectFrom(qGroup)
                .fetchFirst();

        // then
        assertThat(foundGroup).isNotNull();
        assertThat(foundGroup.getGroupId()).isNotBlank();

        log.info("groupId = {}", foundGroup.getGroupId());
    }
}
