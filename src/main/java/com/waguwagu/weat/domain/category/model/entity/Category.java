package com.waguwagu.weat.domain.category.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "category")
@Comment("카테고리")
public class Category {

    @Comment("카테고리 식별자")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Comment("카테고리 명")
    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_order")
    @Comment("카테고리순서")
    private Integer categoryOrder;

    @Column(name = "category_depth")
    @Comment("카테고리깊이")
    private Integer categoryDepth;

    @Builder.Default
    @Column(name = "category_version")
    @Comment("카테고리버전")
    private String categoryVersion = "v2";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_parent_id")
    @Comment("카테고리부모")
    private Category categoryParent;
}