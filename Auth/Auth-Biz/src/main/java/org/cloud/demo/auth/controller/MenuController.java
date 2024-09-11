package org.cloud.demo.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cloud.demo.auth.domain.dto.MenuDTO;
import org.cloud.demo.auth.service.MenuService;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单信息
 */
@RequiredArgsConstructor
@RequestMapping("/menu")
@RestController
@Validated
public class MenuController extends BaseController {
    private final MenuService menuService;

    /**
     * 新增菜单
     */
    @PostMapping
    public R<Void> add(@Valid @RequestBody MenuDTO menuDTO) {
        return toAjax(menuService.insertMenu(menuDTO));
    }



}

