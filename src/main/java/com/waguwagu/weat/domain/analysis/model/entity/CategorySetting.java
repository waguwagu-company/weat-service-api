package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category_setting")
@Comment("카테고리 설정")
public class CategorySetting extends AnalysisSettingDetail {

    @Comment("카테고리 식별자")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, columnDefinition = "INTEGER")
    private Category category;

    @Comment("호불호 여부")
    @Column(name = "is_preferred", columnDefinition = "BOOLEAN")
    private Boolean isPreferred;
}