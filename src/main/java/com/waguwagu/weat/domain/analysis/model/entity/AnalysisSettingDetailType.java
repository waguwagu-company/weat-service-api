package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "analysis_setting_detail_type")
@Comment("세부분석설정 유형 엔티티")
public class AnalysisSettingDetailType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_setting_detail_type_id", nullable = false, updatable = false)
    @Comment("세부분석설정 유형 식별자")
    private Long analysisSettingDetailTypeId;

    @Column(name = "analysis_setting_detail_name", length = 50)
    @Comment("세부분석설정 유형명")
    private String analysisSettingDetailName;
}