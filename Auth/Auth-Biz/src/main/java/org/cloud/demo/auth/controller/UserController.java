package org.cloud.demo.auth.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.cloud.demo.auth.domain.dto.UserRegisterDTO;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.cloud.demo.auth.service.UserService;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("user")
@RestController
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;

    @PostMapping("register")
    public R<Void> register(@Validated @RequestBody UserRegisterDTO registerDTO) {
        return toAjax(userService.register(registerDTO));
    }

    @GetMapping("info/{userId}")
    public R<UserVo> getUserInfo(@NotNull(message = "用户ID不能为空")@PathVariable Long userId) {
        return R.ok(userService.getUserInfo(userId));
    }
}
