package org.cloud.demo.common.constants;

/**
 * 流程常量信息
 */
public interface CaptchaConstants {

    /**
     * 验证码有效期（分钟）
     */
    Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 验证码类型，图片
     */
    Integer GRAPHIC = 1;
    /**
     * 验证码类型，图片
     */
    Integer EMAIL = 2;
    /**
     * 验证码类型，图片
     */
    Integer SMS = 3;
}
