package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisResultDetail is a Querydsl query type for AnalysisResultDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisResultDetail extends EntityPathBase<AnalysisResultDetail> {

    private static final long serialVersionUID = 554580044L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisResultDetail analysisResultDetail = new QAnalysisResultDetail("analysisResultDetail");

    public final QAnalysisResult analysisResult;

    public final StringPath analysisResultDetailContent = createString("analysisResultDetailContent");

    public final NumberPath<Integer> analysisResultDetailId = createNumber("analysisResultDetailId", Integer.class);

    public final StringPath analysisResultDetailTemplateMessage = createString("analysisResultDetailTemplateMessage");

    public final ListPath<String, StringPath> analysisResultKeywords = this.<String, StringPath>createList("analysisResultKeywords", String.class, StringPath.class, PathInits.DIRECT2);

    public final QPlace place;

    public QAnalysisResultDetail(String variable) {
        this(AnalysisResultDetail.class, forVariable(variable), INITS);
    }

    public QAnalysisResultDetail(Path<? extends AnalysisResultDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisResultDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisResultDetail(PathMetadata metadata, PathInits inits) {
        this(AnalysisResultDetail.class, metadata, inits);
    }

    public QAnalysisResultDetail(Class<? extends AnalysisResultDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysisResult = inits.isInitialized("analysisResult") ? new QAnalysisResult(forProperty("analysisResult"), inits.get("analysisResult")) : null;
        this.place = inits.isInitialized("place") ? new QPlace(forProperty("place")) : null;
    }

}

