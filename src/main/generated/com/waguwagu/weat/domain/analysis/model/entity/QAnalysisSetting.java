package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalysisSetting is a Querydsl query type for AnalysisSetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisSetting extends EntityPathBase<AnalysisSetting> {

    private static final long serialVersionUID = 947151378L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalysisSetting analysisSetting = new QAnalysisSetting("analysisSetting");

    public final QAnalysis analysis;

    public final NumberPath<Long> analysisSettingId = createNumber("analysisSettingId", Long.class);

    public final com.waguwagu.weat.domain.group.model.entity.QMember member;

    public QAnalysisSetting(String variable) {
        this(AnalysisSetting.class, forVariable(variable), INITS);
    }

    public QAnalysisSetting(Path<? extends AnalysisSetting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalysisSetting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalysisSetting(PathMetadata metadata, PathInits inits) {
        this(AnalysisSetting.class, metadata, inits);
    }

    public QAnalysisSetting(Class<? extends AnalysisSetting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.analysis = inits.isInitialized("analysis") ? new QAnalysis(forProperty("analysis"), inits.get("analysis")) : null;
        this.member = inits.isInitialized("member") ? new com.waguwagu.weat.domain.group.model.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

