package com.waguwagu.weat.domain.analysis.handler;

import com.waguwagu.weat.domain.analysis.event.AnalysisStartEvent;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;
import com.waguwagu.weat.domain.analysis.service.AnalysisAsyncExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AnalysisStartEventHandler {
    private final AnalysisAsyncExecutor executor;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAnalysisStartEvent(AnalysisStartEvent event) {
        executor.startAnalysisAsync(
                AIAnalysisDTO.Request.builder()
                        .groupId(event.groupId())
                        .analysisId(event.analysisId())
                        .memberSettingList(event.memberSettingList())
                        .build()
        );
    }
}
