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
    @Column(name = "category_id", columnDefinition = "INTEGER", nullable = false, updatable = false)
    private Long categoryId;

    @Comment("카테고리 명")
    @Column(name = "category_name", columnDefinition = "VARCHAR(50)", length = 50)
    private String categoryName;

    @Column(name = "category_order")
    @Comment("카테고리순서")
    private Long categoryOrder;
}