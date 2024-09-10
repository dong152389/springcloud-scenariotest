package org.cloud.demo.workflow.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.cloud.demo.common.web.domain.R;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    private final RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("satoken");
        String userId = (String) StpUtil.getLoginIdByToken(token);
        R<String> r;
        if (userId == null) {
            log.error("用户未登录");
            r = R.fail(HttpStatus.HTTP_UNAUTHORIZED, "用户未登录");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(r));
            return false;
        }
        UserVo userInfo = (UserVo) redisTemplate.opsForValue().get(token);
        if (ObjectUtil.isNull(userInfo)) {
            log.error("用户未登录");
            r = R.fail(HttpStatus.HTTP_UNAUTHORIZED, "用户未登录");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(r));
            return false;
        }
        StpUtil.getSession().set("userInfo", userInfo);
        return true;
    }
}
