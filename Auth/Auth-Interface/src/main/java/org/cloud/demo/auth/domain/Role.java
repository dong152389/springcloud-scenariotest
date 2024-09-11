package org.cloud.demo.auth.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cloud.demo.common.domain.BaseEntity;

/**
 * 角色信息表
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色信息表")
@TableName(value = "sys_role")
@Data
public class Role extends BaseEntity {
    /**
     * 角色ID
     */
    @TableId(value = "role_id", type = IdType.ASSIGN_ID)
    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为null")
    private Long roleId;

    /**
     * 角色名称
     */
    @TableField(value = "role_name")
    @Schema(description = "角色名称")
    @Size(max = 30, message = "角色名称最大长度要小于 30")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @TableField(value = "role_key")
    @Schema(description = "角色权限字符串")
    @Size(max = 100, message = "角色权限字符串最大长度要小于 100")
    @NotBlank(message = "角色权限字符串不能为空")
    private String roleKey;

    /**
     * 显示顺序
     */
    @TableField(value = "role_sort")
    @Schema(description = "显示顺序")
    @NotNull(message = "显示顺序不能为null")
    private Integer roleSort;

    /**
     * 角色状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description = "角色状态（0正常 1停用）")
    @Size(max = 1, message = "角色状态（0正常 1停用）最大长度要小于 1")
    @NotBlank(message = "角色状态（0正常 1停用）不能为空")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableField(value = "del_flag")
    @Schema(description = "删除标志（0代表存在 2代表删除）")
    @Size(max = 1, message = "删除标志（0代表存在 2代表删除）最大长度要小于 1")
    private String delFlag;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    @Size(max = 500, message = "备注最大长度要小于 500")
    private String remark;
}