package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysis is a Querydsl query type for Analysis
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysis extends EntityPathBase<Analysis> {

    private static final long serialVersionUID = -1938293154L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysis analysis = new QAnalysis("analysis");

    public final NumberPath<Long> analysisId = createNumber("analysisId", Long.class);

    public final EnumPath<AnalysisStatus> analysisStatus = createEnum("analysisStatus", AnalysisStatus.class);

    public final com.waguwagu.weat.domain.group.model.entity.QGroup group;

    public QAnalysis(String variable) {
        this(Analysis.class, forVariable(variable), INITS);
    }

    public QAnalysis(Path<? extends Analysis> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysis(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysis(PathMetadata metadata, PathInits inits) {
        this(Analysis.class, metadata, inits);
    }

    public QAnalysis(Class<? extends Analysis> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.group = inits.isInitialized("group") ? new com.waguwagu.weat.domain.group.model.entity.QGroup(forProperty("group")) : null;
    }

}

