package org.cloud.demo.auth.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户和角色关联表
 */
@Schema(description = "用户和角色关联表")
@Data
@TableName(value = "sys_user_role")
public class UserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = -4844435384061898921L;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID")
    private Long roleId;
}
