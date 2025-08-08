package com.waguwagu.weat.domain.category.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "category_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CategoryTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_tag_id")
    @Comment("카테고리 식별자")
    private Long categoryTagId;

    @Column(name = "category_tag_name", length = 50)
    @Comment("카테고리 태그명")
    private String categoryTagName;

    @Column(name = "category_tag_order")
    @Comment("카테고리 태그순서")
    private Long categoryTagOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "category_tag_category_fk"))
    @Comment("카테고리")
    private Category category;
}