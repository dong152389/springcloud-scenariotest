package org.cloud.demo.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import org.cloud.demo.auth.domain.dto.LoginDTO;

public interface LoginService {
    SaTokenInfo login(LoginDTO loginDto);
}
