package com.waguwagu.weat.domain.analysis.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategorySetting is a Querydsl query type for CategorySetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategorySetting extends EntityPathBase<CategorySetting> {

    private static final long serialVersionUID = 1312460208L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategorySetting categorySetting = new QCategorySetting("categorySetting");

    public final QAnalysisSettingDetail _super;

    // inherited
    public final QAnalysisSetting analysisSetting;

    //inherited
    public final NumberPath<Long> analysisSettingDetailId;

    public final com.waguwagu.weat.domain.category.model.entity.QCategory category;

    public final com.waguwagu.weat.domain.category.model.entity.QCategoryTag categoryTag;

    public final BooleanPath isPreferred = createBoolean("isPreferred");

    public QCategorySetting(String variable) {
        this(CategorySetting.class, forVariable(variable), INITS);
    }

    public QCategorySetting(Path<? extends CategorySetting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategorySetting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategorySetting(PathMetadata metadata, PathInits inits) {
        this(CategorySetting.class, metadata, inits);
    }

    public QCategorySetting(Class<? extends CategorySetting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAnalysisSettingDetail(type, metadata, inits);
        this.analysisSetting = _super.analysisSetting;
        this.analysisSettingDetailId = _super.analysisSettingDetailId;
        this.category = inits.isInitialized("category") ? new com.waguwagu.weat.domain.category.model.entity.QCategory(forProperty("category")) : null;
        this.categoryTag = inits.isInitialized("categoryTag") ? new com.waguwagu.weat.domain.category.model.entity.QCategoryTag(forProperty("categoryTag"), inits.get("categoryTag")) : null;
    }

}

