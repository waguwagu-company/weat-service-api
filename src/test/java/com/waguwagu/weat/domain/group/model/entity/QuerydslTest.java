package com.waguwagu.weat.domain.group.model.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class QuerydslTest {
    @PersistenceContext
    EntityManager em;

    @Test
    void querydslTest() {
        // TODO: 테스트 미완료 (DB 서버 세팅 후 .env 변경하여 테스트 완료하기)
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

        System.out.println("groupId = " + foundGroup.getGroupId());
    }
}
