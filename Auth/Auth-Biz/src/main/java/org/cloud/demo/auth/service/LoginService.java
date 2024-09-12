package org.cloud.demo.auth.service;

import org.cloud.demo.auth.domain.dto.LoginDTO;

public interface LoginService {
    String login(LoginDTO loginDto);
}
