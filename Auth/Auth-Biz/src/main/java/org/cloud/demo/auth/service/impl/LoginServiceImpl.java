package org.cloud.demo.auth.service.impl;

import org.cloud.demo.auth.domain.dto.LoginDTO;
import org.cloud.demo.auth.service.LoginService;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Override
    public boolean login(LoginDTO loginDto) {
        return false;
    }
}
