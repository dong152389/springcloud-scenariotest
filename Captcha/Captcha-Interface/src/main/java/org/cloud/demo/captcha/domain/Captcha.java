package org.cloud.demo.captcha.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "验证码")
public class Captcha {
    @Schema(title = "唯一标识，用来校验验证码")
    private String uuid;
    @Schema(title = "验证码内容")
    private String code;
    @Schema(title = "验证码类型,（图片，短信，邮件等等）")
    private Integer type;
}
