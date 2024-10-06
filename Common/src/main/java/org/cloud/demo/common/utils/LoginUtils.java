package org.cloud.demo.common.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import org.cloud.demo.common.domain.LoginUser;
import org.cloud.demo.common.domain.RoleDTO;

import java.util.List;

public class LoginUtils {

    public static LoginUser getLoginUser() {
        return (LoginUser) StpUtil.getTokenSession().get("loginUser");
    }

    public static String getUserName() {
        return getLoginUser().getUsername();
    }

    public static List<String> getRoleIds() {
        List<RoleDTO> roles = getLoginUser().getRoles();
        if (CollUtil.isNotEmpty(roles)) {
            return roles.stream().map(RoleDTO::getRoleId).map(Object::toString).toList();

        }
        return List.of();
    }
}
