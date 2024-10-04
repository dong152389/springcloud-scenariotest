package org.cloud.demo.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.RoleMenu;
import org.cloud.demo.auth.service.RoleService;
import org.cloud.demo.common.domain.RoleDTO;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("role")
@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class RoleController extends BaseController {
    private final RoleService roleService;

    /**
     * 新增角色
     */
    @PostMapping
    public R<Void> add(@Valid @RequestBody RoleDTO roleDTO) {
        return toAjax(roleService.insertRole(roleDTO));
    }

    @PostMapping("menu")
    public R<Void> bindMenu(@Valid RoleMenu roleMenu) {
        return toAjax(roleService.bindMenu(roleMenu));
    }
}
