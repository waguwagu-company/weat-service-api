package com.waguwagu.weat.domain.group.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "\"group\"")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @Column(name = "group_id", length = 32, nullable = false, updatable = false)
    private String groupId;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdAt;

    @Column(name = "is_single_member_group", nullable = false)
    private boolean isSingleMemberGroup;

    @PrePersist
    public void prePersist() {
        this.groupId = java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
