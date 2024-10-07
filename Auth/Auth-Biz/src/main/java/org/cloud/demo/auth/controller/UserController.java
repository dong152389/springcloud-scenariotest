package org.cloud.demo.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cloud.demo.auth.component.LoginHelper;
import org.cloud.demo.auth.domain.dto.UserRegisterDTO;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.cloud.demo.auth.service.UserService;
import org.cloud.demo.common.domain.LoginUser;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("user")
@RestController
@RequiredArgsConstructor
@Validated
public class UserController extends BaseController {
    private final UserService userService;

    @PostMapping("register")
    public R<Void> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        return toAjax(userService.register(registerDTO));
    }

    @GetMapping("info")
    public R<Map<String, Object>> getInfo() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        UserVo user = userService.getUserInfo(loginUser.getUserId());
        Map<String, Object> ajax = new HashMap<>();
        ajax.put("user", user);
        ajax.put("roles", loginUser.getRolePermission());
        ajax.put("permissions", loginUser.getMenuPermission());
        return R.ok(ajax);
    }
}
