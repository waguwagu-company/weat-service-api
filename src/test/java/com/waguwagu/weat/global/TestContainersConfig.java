package com.waguwagu.weat.global;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;


/**
 * Spring Boot 3.1+ / Testcontainers 연동용 설정 클래스.
 *
 * - @Bean + @ServiceConnection: 부트가 이 컨테이너 Bean을 자동 기동하고 DataSource로 연결함.
 * - 정적 필드(static)나 수동 start() 호출, @DynamicPropertySource, @Testcontainers 애노테이션 필요 없음.
 * - 여러 테스트 클래스에서 @Import(PostgresTcTestConfig.class)로 재사용 가능.
 */
@TestConfiguration
public class TestContainersConfig {

    /**
     * static 필드로 선언하여 한 번만 컨테이너를 띄우고 여러 테스트 간에 같은 컨테이너 재사용.
     * @Transactional, @DirtiesContext 등을 통해 상태 관리 필요
     */
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return POSTGRES;
    }

}

