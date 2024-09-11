package org.cloud.demo.auth.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色和菜单关联表
 */
@Schema(description="角色和菜单关联表")
@Data
@TableName(value = "sys_role_menu")
public class RoleMenu {
    /**
     * 角色ID
     */
    @Schema(description="角色ID")
    @NotNull(message = "角色ID不能为null")
    private Long roleId;

    /**
     * 菜单ID
     */
    @Schema(description="菜单ID")
    @NotNull(message = "菜单ID不能为null")
    private Long menuId;
}