package com.waguwagu.weat.domain.group.model.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.waguwagu.weat.global.TestContainersConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
class QuerydslTest {

    @Autowired
    JPAQueryFactory queryFactory;

    @Test
    void querydslTest() {
        QGroup qGroup = QGroup.group;
        Group foundGroup = queryFactory.selectFrom(qGroup).fetchFirst();

        assertThat(foundGroup).isNotNull();
        assertThat(foundGroup.getGroupId()).isNotBlank();
        log.info("groupId = {}", foundGroup.getGroupId());
    }
}

