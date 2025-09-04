package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTextInputSetting is a Querydsl query type for TextInputSetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTextInputSetting extends EntityPathBase<TextInputSetting> {

    private static final long serialVersionUID = 960577141L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTextInputSetting textInputSetting = new QTextInputSetting("textInputSetting");

    public final QAnalysisSettingDetail _super;

    // inherited
    public final QAnalysisSetting analysisSetting;

    //inherited
    public final NumberPath<Long> analysisSettingDetailId;

    public final StringPath inputText = createString("inputText");

    public QTextInputSetting(String variable) {
        this(TextInputSetting.class, forVariable(variable), INITS);
    }

    public QTextInputSetting(Path<? extends TextInputSetting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTextInputSetting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTextInputSetting(PathMetadata metadata, PathInits inits) {
        this(TextInputSetting.class, metadata, inits);
    }

    public QTextInputSetting(Class<? extends TextInputSetting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAnalysisSettingDetail(type, metadata, inits);
        this.analysisSetting = _super.analysisSetting;
        this.analysisSettingDetailId = _super.analysisSettingDetailId;
    }

}

