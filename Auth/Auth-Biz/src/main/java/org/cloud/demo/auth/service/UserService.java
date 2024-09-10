package org.cloud.demo.auth.service;

import jakarta.validation.constraints.NotNull;
import org.cloud.demo.auth.domain.dto.UserRegisterDTO;
import org.cloud.demo.auth.domain.vo.UserVo;

public interface UserService {
    int register(UserRegisterDTO registerDTO);

    UserVo getUserInfo(@NotNull(message = "用户ID不能为空") Long userId);
}
