package com.waguwagu.weat.domain.analysis.handler;

import com.waguwagu.weat.domain.analysis.event.AnalysisStartEvent;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;
import com.waguwagu.weat.domain.analysis.service.AnalysisAsyncExecutor;
import com.waguwagu.weat.global.TestContainersConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import({TestContainersConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
public class AnalysisStartEventHandlerTest {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @SpyBean
    AnalysisStartEventHandler analysisStartEventHandler;

    @SpyBean
    AnalysisAsyncExecutor analysisAsyncExecutor;

    @Nested
    @DisplayName("handleAnalysisStartEvent")
    class HandleAnalysisStartEvent {

        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {

            @Test
            @DisplayName("이벤트 발행 후 트랜잭션이 커밋되면 이벤트 핸들러가 실행되어야 한다.")
            void handleAnalysisStartEvent_success_tc_01() {
                // given
                final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);

                final List<AIAnalysisDTO.Request.MemberSetting> givenMemberSettingList =
                        List.of(mock(AIAnalysisDTO.Request.MemberSetting.class),
                                mock(AIAnalysisDTO.Request.MemberSetting.class));

                final AnalysisStartEvent givenEvent = new AnalysisStartEvent("test", 0L, givenMemberSettingList);

                doNothing().when(analysisAsyncExecutor)
                        .startAnalysisAsync(any(AIAnalysisDTO.Request.class));

                // when
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    applicationEventPublisher.publishEvent(givenEvent);
                });

                // then
                final ArgumentCaptor<AIAnalysisDTO.Request> aiAnalysisRequestCaptor = ArgumentCaptor.forClass(AIAnalysisDTO.Request.class);
                verify(analysisStartEventHandler).handleAnalysisStartEvent(givenEvent);
                verify(analysisAsyncExecutor).startAnalysisAsync(aiAnalysisRequestCaptor.capture());

                final AIAnalysisDTO.Request aiAnalysisRequest = aiAnalysisRequestCaptor.getValue();
                assertThat(aiAnalysisRequest.getGroupId()).isEqualTo(givenEvent.groupId());
                assertThat(aiAnalysisRequest.getAnalysisId()).isEqualTo(givenEvent.analysisId());
                assertThat(aiAnalysisRequest.getMemberSettingList()).hasSize(givenMemberSettingList.size());
            }


            @Test
            @DisplayName("이벤트 발행 후 트랜잭션이 롤백되면 이벤트 핸들러가 실행되지 않아야 한다.")
            void handleAnalysisStartEvent_success_tc_02() {
                // given
                final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);

                final List<AIAnalysisDTO.Request.MemberSetting> givenMemberSettingList =
                        List.of(mock(AIAnalysisDTO.Request.MemberSetting.class),
                                mock(AIAnalysisDTO.Request.MemberSetting.class));

                final AnalysisStartEvent givenEvent = new AnalysisStartEvent("test", 0L, givenMemberSettingList);

                // when
                transactionTemplate.executeWithoutResult(
                        transactionStatus -> {
                            applicationEventPublisher.publishEvent(givenEvent);
                            transactionStatus.setRollbackOnly();
                        });

                // then
                verify(analysisStartEventHandler, never()).handleAnalysisStartEvent(any(AnalysisStartEvent.class));
                verify(analysisAsyncExecutor, never()).startAnalysisAsync(any(AIAnalysisDTO.Request.class));
            }

            @Test
            @DisplayName("트랜잭션 밖에서는 이벤트가 발행되어도 핸들러가 실행되지 않아야 한다.")
            void handleAnalysisStartEvent_success_tc_03() {
                // given
                final List<AIAnalysisDTO.Request.MemberSetting> givenMemberSettingList =
                        List.of(mock(AIAnalysisDTO.Request.MemberSetting.class),
                                mock(AIAnalysisDTO.Request.MemberSetting.class));

                final AnalysisStartEvent givenEvent = new AnalysisStartEvent("test", 0L, givenMemberSettingList);

                // when
                applicationEventPublisher.publishEvent(givenEvent);

                // then
                verify(analysisStartEventHandler, never()).handleAnalysisStartEvent(any());
                verify(analysisAsyncExecutor, never()).startAnalysisAsync(any());
            }
        }

        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {
            @Test
            @DisplayName("핸들러 내부에서 예외가 발생해도 트랜잭션에는 영향을 주지 않아야 한다.")
            void handleAnalysisStartEvent_exception_tc_01() {
                // given
                final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                final AnalysisStartEvent givenEvent =
                        new AnalysisStartEvent("test", 0L,
                                List.of(mock(AIAnalysisDTO.Request.MemberSetting.class)));

                doThrow(new RuntimeException("test exception"))
                        .when(analysisStartEventHandler)
                        .handleAnalysisStartEvent(any());

                AtomicBoolean committed = new AtomicBoolean(false);

                // when & then
                assertDoesNotThrow(() ->
                        transactionTemplate.executeWithoutResult(
                                transactionStatus -> {
                                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                                        @Override
                                        public void afterCompletion(int status) {
                                            committed.set(status == TransactionSynchronization.STATUS_COMMITTED);
                                        }
                                    });
                                    applicationEventPublisher.publishEvent(givenEvent);
                                })
                );

                assertThat(committed).isTrue();
                verify(analysisStartEventHandler).handleAnalysisStartEvent(givenEvent);
            }
        }
    }
}
