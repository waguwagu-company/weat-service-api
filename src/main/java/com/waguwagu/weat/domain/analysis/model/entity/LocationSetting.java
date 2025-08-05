package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@DiscriminatorValue("LOCATION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Comment("위치 설정")
@Table(name = "location_setting")
public class LocationSetting extends AnalysisSettingDetail {

    @Column(name = "x_position")
    @Comment("X 좌표")
    private Double xPosition;

    @Column(name = "y_position")
    @Comment("Y 좌표")
    private Double yPosition;

    @Column(name = "roadname_address")
    @Comment("도로명 주소")
    private String roadnameAddress;
}