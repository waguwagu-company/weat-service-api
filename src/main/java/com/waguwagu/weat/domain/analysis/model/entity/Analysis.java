package com.waguwagu.weat.domain.analysis.model.entity;

import org.hibernate.annotations.Comment;import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.waguwagu.weat.domain.group.model.entity.Group;
import java.time.ZonedDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "analysis")
@Comment("분석 엔티티")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id", nullable = false, updatable = false, columnDefinition = "INTEGER")
    @Comment("분석 식별자")
    private Long analysisId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, updatable = false, columnDefinition = "BPCHAR(32)")
    @Comment("그룹 정보")
    @ToString.Exclude
    private Group group;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_status", nullable = false, columnDefinition = "VARCHAR(50)")
    @Comment("분석 상태")
    private AnalysisStatus analysisStatus = AnalysisStatus.NOT_STARTED;

    @ToString.Include(name = "groupId")
    public String getGroupIdOnly() {
        return group != null ? group.getGroupId() : null;
    }
}