package org.cloud.demo.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.RoleMenu;
import org.cloud.demo.auth.domain.vo.RoleVo;
import org.cloud.demo.auth.service.RoleService;
import org.cloud.demo.common.domain.RoleDTO;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("role")
@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@Tag(name = "用户角色相关", description = "RoleController")
public class RoleController extends BaseController {
    private final RoleService roleService;

    /**
     * 新增角色
     */
    @PostMapping
    public R<Void> add(@Valid @RequestBody RoleDTO roleDTO) {
        return toAjax(roleService.insertRole(roleDTO));
    }

    /**
     * 绑定菜单
     *
     * @param roleMenu 角色菜单对象
     * @return 返回结果
     */
    @PostMapping("menu")
    public R<Void> bindMenu(@Valid RoleMenu roleMenu) {
        return toAjax(roleService.bindMenu(roleMenu));
    }


    /**
     * 根据角色ID集合查询角色信息
     *
     * @param roleIds 角色ID列表
     * @return 角色信息列表
     */
    @GetMapping("queryRolesByRoleIds")
    public R<List<RoleVo>> queryRolesByRoleIds(@RequestParam List<Long> roleIds) {
        List<RoleVo> roleVos = roleService.queryRolesByRoleIds(roleIds);
        return R.ok(roleVos);
    }

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    @GetMapping("queryRoleByRoleId")
    public R<RoleVo> queryRoleByRoleId(@RequestParam Long roleId) {
        RoleVo roleVos = roleService.queryRoleByRoleId(roleId);
        return R.ok(roleVos);
    }

    /**
     * 批量选择用户授权
     *
     * @param roleId  角色ID
     * @param userIds 用户ID串
     */
    @PutMapping("/authUser")
    @Operation(summary = "批量选择用户授权", parameters = {
            @Parameter(description = "角色id", required = true),
            @Parameter(description = "用户ids", required = true)}
    )
    public R<Void> selectAuthUser(@NotNull(message = "角色ID不能为空") @RequestParam Long roleId, @NotNull(message = "用户ID不能为空") @RequestParam Long[] userIds) {
        return toAjax(roleService.selectAuthUser(roleId, userIds));
    }
}
