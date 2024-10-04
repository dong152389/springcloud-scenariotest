package org.cloud.demo.common.utils;

import cn.dev33.satoken.stp.StpUtil;
import org.cloud.demo.common.domain.LoginUser;

public class LoginUtils {

    public static LoginUser getLoginUser() {
        return (LoginUser) StpUtil.getTokenSession().get("loginUser");
    }

    public static String getUserName() {
        return getLoginUser().getUsername();
    }
}
