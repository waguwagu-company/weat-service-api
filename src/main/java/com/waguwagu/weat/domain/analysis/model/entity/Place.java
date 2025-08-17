package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Integer placeId;

    @Column(name = "place_name", length = 255)
    private String placeName;

    @Column(name = "place_roadname_address", length = 255)
    private String placeRoadnameAddress;

    @Column(name = "place_url")
    private String placeUrl;
}