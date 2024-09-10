package org.cloud.demo.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cloud.demo.common.domain.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Schema
@Data
@TableName(value = "wf_category")
public class WfCategory extends BaseEntity {

    private static final long serialVersionUID = 1654138690130649267L;
    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "分类ID")
    private Long id;

    /**
     * 分类名称
     */
    @TableField(value = "`name`")
    @Schema(description = "分类名称")
    private String name;

    /**
     * 分类描述
     */
    @TableField(value = "`desc`")
    @Schema(description = "分类描述")
    private String desc;


    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableField(value = "del_flag")
    @Schema(description = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
}