package com.waguwagu.weat.domain.template.repository;

import com.waguwagu.weat.domain.template.model.entity.AIMessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AITemplateMessage extends JpaRepository<AIMessageTemplate, Long> {
}
