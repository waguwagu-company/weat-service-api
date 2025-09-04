package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisResultLike is a Querydsl query type for AnalysisResultLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisResultLike extends EntityPathBase<AnalysisResultLike> {

    private static final long serialVersionUID = -1916497326L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisResultLike analysisResultLike = new QAnalysisResultLike("analysisResultLike");

    public final QAnalysisResultDetail analysisResultDetail;

    public final NumberPath<Long> analysisResultLikeId = createNumber("analysisResultLikeId", Long.class);

    public final com.waguwagu.weat.domain.group.model.entity.QMember member;

    public QAnalysisResultLike(String variable) {
        this(AnalysisResultLike.class, forVariable(variable), INITS);
    }

    public QAnalysisResultLike(Path<? extends AnalysisResultLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisResultLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisResultLike(PathMetadata metadata, PathInits inits) {
        this(AnalysisResultLike.class, metadata, inits);
    }

    public QAnalysisResultLike(Class<? extends AnalysisResultLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysisResultDetail = inits.isInitialized("analysisResultDetail") ? new QAnalysisResultDetail(forProperty("analysisResultDetail"), inits.get("analysisResultDetail")) : null;
        this.member = inits.isInitialized("member") ? new com.waguwagu.weat.domain.group.model.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

