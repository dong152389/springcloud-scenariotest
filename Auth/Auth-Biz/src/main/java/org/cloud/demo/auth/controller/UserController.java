package org.cloud.demo.auth.controller;

import lombok.RequiredArgsConstructor;
import org.cloud.demo.auth.domain.dto.UserRegisterDTO;
import org.cloud.demo.auth.service.UserService;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user")
@RestController
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;

    @PostMapping("register")
    public R<Void> register(@Validated @RequestBody UserRegisterDTO registerDTO) {
        return toAjax(userService.register(registerDTO));
    }
}
