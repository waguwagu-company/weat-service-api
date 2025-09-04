package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisSettingDetail is a Querydsl query type for AnalysisSettingDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisSettingDetail extends EntityPathBase<AnalysisSettingDetail> {

    private static final long serialVersionUID = 1713508611L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisSettingDetail analysisSettingDetail = new QAnalysisSettingDetail("analysisSettingDetail");

    public final QAnalysisSetting analysisSetting;

    public final NumberPath<Long> analysisSettingDetailId = createNumber("analysisSettingDetailId", Long.class);

    public QAnalysisSettingDetail(String variable) {
        this(AnalysisSettingDetail.class, forVariable(variable), INITS);
    }

    public QAnalysisSettingDetail(Path<? extends AnalysisSettingDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisSettingDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisSettingDetail(PathMetadata metadata, PathInits inits) {
        this(AnalysisSettingDetail.class, metadata, inits);
    }

    public QAnalysisSettingDetail(Class<? extends AnalysisSettingDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysisSetting = inits.isInitialized("analysisSetting") ? new QAnalysisSetting(forProperty("analysisSetting"), inits.get("analysisSetting")) : null;
    }

}

