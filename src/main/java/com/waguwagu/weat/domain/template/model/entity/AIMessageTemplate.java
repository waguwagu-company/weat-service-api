package com.waguwagu.weat.domain.template.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ai_message_template")
public class AIMessageTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_message_template_id")
    private Integer messageTemplateId;

    @Column(name = "ai_message_template_title")
    private String messageTemplateTitle;

    @Column(name = "ai_message_template_content")
    private String messageTemplateContent;
}