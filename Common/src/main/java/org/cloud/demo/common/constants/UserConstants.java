package org.cloud.demo.common.constants;

/**
 * 用户常量信息
 */
public interface UserConstants {

    /**
     * 用户名长度限制
     */
    int USERNAME_MIN_LENGTH = 2;
    int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    int PASSWORD_MIN_LENGTH = 5;
    int PASSWORD_MAX_LENGTH = 20;

    /**
     * 管理员ID
     */
    Long ADMIN_ID = 1L;
    /**
     * token过期时间，单位天
     */
    int TOKEN_EXPIRE = 1;

}
