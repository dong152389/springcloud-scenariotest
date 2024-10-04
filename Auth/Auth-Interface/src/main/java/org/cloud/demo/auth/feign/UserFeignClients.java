package org.cloud.demo.auth.feign;

import jakarta.validation.constraints.NotNull;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserFeignClients {
    @GetMapping("user/info/{userId}")
    UserVo getUserInfo(@NotNull(message = "用户ID不能为空") @PathVariable Long userId);
}
