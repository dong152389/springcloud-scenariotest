package org.cloud.demo.auth.controller;

import lombok.RequiredArgsConstructor;
import org.cloud.demo.auth.domain.dto.LoginDTO;
import org.cloud.demo.auth.service.LoginService;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/login")
@RestController
@RequiredArgsConstructor
public class LoginController extends BaseController {
    private final LoginService loginService;

    public R<Void> login(@Validated @RequestBody LoginDTO loginDto) {
        return toAjax(loginService.login(loginDto));
    }
}
