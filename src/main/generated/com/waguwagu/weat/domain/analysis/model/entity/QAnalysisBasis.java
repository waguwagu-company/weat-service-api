package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisBasis is a Querydsl query type for AnalysisBasis
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisBasis extends EntityPathBase<AnalysisBasis> {

    private static final long serialVersionUID = -1190252416L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisBasis analysisBasis = new QAnalysisBasis("analysisBasis");

    public final StringPath analysisBasisContent = createString("analysisBasisContent");

    public final NumberPath<Integer> analysisBasisId = createNumber("analysisBasisId", Integer.class);

    public final StringPath analysisBasisType = createString("analysisBasisType");

    public final QAnalysisResultDetail analysisResultDetail;

    public final NumberPath<Integer> analysisScore = createNumber("analysisScore", Integer.class);

    public QAnalysisBasis(String variable) {
        this(AnalysisBasis.class, forVariable(variable), INITS);
    }

    public QAnalysisBasis(Path<? extends AnalysisBasis> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisBasis(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisBasis(PathMetadata metadata, PathInits inits) {
        this(AnalysisBasis.class, metadata, inits);
    }

    public QAnalysisBasis(Class<? extends AnalysisBasis> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysisResultDetail = inits.isInitialized("analysisResultDetail") ? new QAnalysisResultDetail(forProperty("analysisResultDetail"), inits.get("analysisResultDetail")) : null;
    }

}

