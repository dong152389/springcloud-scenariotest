package org.cloud.demo.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleDTO {
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    @Size(max = 30, message = "角色名称最大长度要小于 30")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @Schema(description = "角色权限字符串")
    @Size(max = 100, message = "角色权限字符串最大长度要小于 100")
    @NotBlank(message = "角色权限字符串不能为空")
    private String roleKey;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    @NotNull(message = "显示顺序不能为null")
    private Integer roleSort;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注最大长度要小于 500")
    private String remark;
}
