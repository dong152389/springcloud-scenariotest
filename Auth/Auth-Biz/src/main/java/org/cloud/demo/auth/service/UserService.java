package org.cloud.demo.auth.service;

import org.cloud.demo.auth.domain.dto.UserRegisterDTO;

public interface UserService {
    int register(UserRegisterDTO registerDTO);
}
