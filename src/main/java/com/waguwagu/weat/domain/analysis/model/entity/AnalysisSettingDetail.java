package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Comment("세부분석설정")
@Table(name = "analysis_setting_detail")
public class AnalysisSettingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_setting_detail_id")
    private Long analysisSettingDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_setting_id", nullable = false)
    @Comment("분석 설정")
    private AnalysisSetting analysisSetting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_setting_detail_type_id", nullable = false)
    @Comment("세부 설정 유형")
    private AnalysisSettingDetailType analysisSettingDetailType;

    @Column(name = "analysis_setting_detail_name")
    @Comment("세부 설정 이름")
    private String analysisSettingDetailName;
}
