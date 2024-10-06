package org.cloud.demo.workflow.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.common.domain.LoginUser;
import org.cloud.demo.common.web.domain.R;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    public static final String LOGIN_USER_KEY = "loginUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoginUser loginUser = (LoginUser) StpUtil.getTokenSession().get(LOGIN_USER_KEY);
        if (loginUser == null) {
            log.error("用户未登录");
            R<String> r = R.fail(HttpStatus.HTTP_UNAUTHORIZED, "用户未登录");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(r));
            return false;
        }
        return true;
    }
}
