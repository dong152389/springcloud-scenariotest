package org.cloud.demo.captcha.properties;

import lombok.Data;
import org.cloud.demo.captcha.enums.CaptchaCategory;
import org.cloud.demo.captcha.enums.CaptchaType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码 配置属性
 *
 * @author Lion Li
 */
@Data
@Component
@ConfigurationProperties(prefix = "captcha.graphic")
public class GraphicCaptchaProperties {

    /**
     * 验证码类型
     */
    private CaptchaType type;

    /**
     * 验证码类别
     */
    private CaptchaCategory category;

    /**
     * 数字验证码位数
     */
    private Integer numberLength;

    /**
     * 字符验证码长度
     */
    private Integer charLength;
}
