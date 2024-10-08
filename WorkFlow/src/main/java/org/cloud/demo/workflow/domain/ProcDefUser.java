package org.cloud.demo.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 流程和用户组关联表
 */
@Schema(description = "流程和用户组关联表")
@Data
@TableName(value = "proc_def_user")
public class ProcDefUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 5032495082604593532L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    @NotNull(message = "主键不能为null")
    private Integer id;

    /**
     * 流程定义ID
     */
    @TableField(value = "proc_def_id")
    @Schema(description = "流程定义ID")
    @Size(max = 255, message = "流程定义ID最大长度要小于 255")
    @NotBlank(message = "流程定义ID不能为空")
    private String procDefId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为null")
    private Long userId;

}