package com.waguwagu.weat.domain.category.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategoryTag is a Querydsl query type for CategoryTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategoryTag extends EntityPathBase<CategoryTag> {

    private static final long serialVersionUID = -1424117992L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategoryTag categoryTag = new QCategoryTag("categoryTag");

    public final QCategory category;

    public final NumberPath<Long> categoryTagId = createNumber("categoryTagId", Long.class);

    public final StringPath categoryTagName = createString("categoryTagName");

    public final NumberPath<Long> categoryTagOrder = createNumber("categoryTagOrder", Long.class);

    public QCategoryTag(String variable) {
        this(CategoryTag.class, forVariable(variable), INITS);
    }

    public QCategoryTag(Path<? extends CategoryTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategoryTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategoryTag(PathMetadata metadata, PathInits inits) {
        this(CategoryTag.class, metadata, inits);
    }

    public QCategoryTag(Class<? extends CategoryTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
    }

}

