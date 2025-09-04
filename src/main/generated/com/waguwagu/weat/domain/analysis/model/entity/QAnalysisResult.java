package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisResult is a Querydsl query type for AnalysisResult
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisResult extends EntityPathBase<AnalysisResult> {

    private static final long serialVersionUID = -2076314597L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisResult analysisResult = new QAnalysisResult("analysisResult");

    public final QAnalysis analysis;

    public final NumberPath<Long> analysisResultId = createNumber("analysisResultId", Long.class);

    public final com.waguwagu.weat.domain.group.model.entity.QGroup group;

    public QAnalysisResult(String variable) {
        this(AnalysisResult.class, forVariable(variable), INITS);
    }

    public QAnalysisResult(Path<? extends AnalysisResult> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisResult(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisResult(PathMetadata metadata, PathInits inits) {
        this(AnalysisResult.class, metadata, inits);
    }

    public QAnalysisResult(Class<? extends AnalysisResult> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysis = inits.isInitialized("analysis") ? new QAnalysis(forProperty("analysis"), inits.get("analysis")) : null;
        this.group = inits.isInitialized("group") ? new com.waguwagu.weat.domain.group.model.entity.QGroup(forProperty("group")) : null;
    }

}

