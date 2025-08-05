package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import com.waguwagu.weat.domain.group.model.entity.Member;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "analysis_setting")
@Comment("분석 설정 엔티티")
public class AnalysisSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_setting_id", nullable = false, updatable = false, columnDefinition = "INTEGER")
    @Comment("분석설정식별자")
    private Long analysisSettingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false, columnDefinition = "INTEGER")
    @Comment("분석 정보")
    @ToString.Exclude
    private Analysis analysis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, columnDefinition = "INTEGER")
    @Comment("멤버 정보")
    @ToString.Exclude
    private Member member;

    @ToString.Include(name = "analysisId")
    public Long getAnalysisIdOnly() {
        return analysis != null ? analysis.getAnalysisId() : null;
    }

    @ToString.Include(name = "memberId")
    public Long getMemberIdOnly() {
        return member != null ? member.getMemberId() : null;
    }
}