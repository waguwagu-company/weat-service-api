package com.waguwagu.weat.domain.template.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAIMessageTemplate is a Querydsl query type for AIMessageTemplate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAIMessageTemplate extends EntityPathBase<AIMessageTemplate> {

    private static final long serialVersionUID = 2099842521L;

    public static final QAIMessageTemplate aIMessageTemplate = new QAIMessageTemplate("aIMessageTemplate");

    public final StringPath messageTemplateContent = createString("messageTemplateContent");

    public final NumberPath<Integer> messageTemplateId = createNumber("messageTemplateId", Integer.class);

    public final StringPath messageTemplateTitle = createString("messageTemplateTitle");

    public QAIMessageTemplate(String variable) {
        super(AIMessageTemplate.class, forVariable(variable));
    }

    public QAIMessageTemplate(Path<? extends AIMessageTemplate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAIMessageTemplate(PathMetadata metadata) {
        super(AIMessageTemplate.class, metadata);
    }

}

