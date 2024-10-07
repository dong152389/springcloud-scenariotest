package org.cloud.demo.auth.service;

import org.cloud.demo.auth.domain.dto.UserRegisterDTO;
import org.cloud.demo.auth.domain.vo.UserVo;

public interface UserService {
    int register(UserRegisterDTO registerDTO);

    UserVo getUserInfo(Long userId);

}
