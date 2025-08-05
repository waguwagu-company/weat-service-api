package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;


@Getter
@SuperBuilder
@DiscriminatorValue("TEXT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Comment("텍스트 입력 설정")
@Entity
@Table(name = "text_input_setting")
public class TextInputSetting extends AnalysisSettingDetail {

    @Column(name = "input_text")
    @Comment("입력 텍스트")
    private String inputText;
}