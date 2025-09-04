package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAnalysisSettingDetailType is a Querydsl query type for AnalysisSettingDetailType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisSettingDetailType extends EntityPathBase<AnalysisSettingDetailType> {

    private static final long serialVersionUID = 1963186909L;

    public static final QAnalysisSettingDetailType analysisSettingDetailType = new QAnalysisSettingDetailType("analysisSettingDetailType");

    public final StringPath analysisSettingDetailName = createString("analysisSettingDetailName");

    public final NumberPath<Long> analysisSettingDetailTypeId = createNumber("analysisSettingDetailTypeId", Long.class);

    public QAnalysisSettingDetailType(String variable) {
        super(AnalysisSettingDetailType.class, forVariable(variable));
    }

    public QAnalysisSettingDetailType(Path<? extends AnalysisSettingDetailType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAnalysisSettingDetailType(PathMetadata metadata) {
        super(AnalysisSettingDetailType.class, metadata);
    }

}

