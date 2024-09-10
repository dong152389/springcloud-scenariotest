package org.cloud.demo.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.cloud.demo.common.domain.BaseEntity;

/**
 * 流程表单信息表
 */
@Schema(description = "流程表单信息表")
@Data
@TableName(value = "wf_form")
public class WfForm extends BaseEntity {
    /**
     * 表单主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "表单主键")
    private Long id;

    /**
     * 表单名称
     */
    @TableField(value = "`name`")
    @Schema(description = "表单名称")
    private String name;

    /**
     * 表单内容
     */
    @TableField(value = "content")
    @Schema(description = "表单内容")
    private String content;

    /**
     * 备注
     */
    @TableField(value = "`desc`")
    @Schema(description = "备注")
    private String desc;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableField(value = "del_flag")
    @Schema(description = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
}