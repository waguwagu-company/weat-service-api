package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLocationSetting is a Querydsl query type for LocationSetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLocationSetting extends EntityPathBase<LocationSetting> {

    private static final long serialVersionUID = 2114092825L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLocationSetting locationSetting = new QLocationSetting("locationSetting");

    public final QAnalysisSettingDetail _super;

    // inherited
    public final QAnalysisSetting analysisSetting;

    //inherited
    public final NumberPath<Long> analysisSettingDetailId;

    public final StringPath roadnameAddress = createString("roadnameAddress");

    public final NumberPath<Double> xPosition = createNumber("xPosition", Double.class);

    public final NumberPath<Double> yPosition = createNumber("yPosition", Double.class);

    public QLocationSetting(String variable) {
        this(LocationSetting.class, forVariable(variable), INITS);
    }

    public QLocationSetting(Path<? extends LocationSetting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLocationSetting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLocationSetting(PathMetadata metadata, PathInits inits) {
        this(LocationSetting.class, metadata, inits);
    }

    public QLocationSetting(Class<? extends LocationSetting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAnalysisSettingDetail(type, metadata, inits);
        this.analysisSetting = _super.analysisSetting;
        this.analysisSettingDetailId = _super.analysisSettingDetailId;
    }

}

