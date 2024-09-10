package org.cloud.demo.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程实例关联表单
 */
@Schema(description="流程实例关联表单")
@Data
@TableName(value = "wf_deploy_form")
public class WfDeployForm {
    /**
     * 流程实例主键
     */
    @TableField(value = "deploy_id")
    @Schema(description="流程实例主键")
    private String deployId;

    /**
     * 表单Key
     */
    @TableField(value = "form_key")
    @Schema(description="表单Key")
    private String formKey;

    /**
     * 节点Key
     */
    @TableField(value = "node_key")
    @Schema(description="节点Key")
    private String nodeKey;

    /**
     * 表单名称
     */
    @TableField(value = "form_name")
    @Schema(description="表单名称")
    private String formName;

    /**
     * 节点名称
     */
    @TableField(value = "node_name")
    @Schema(description="节点名称")
    private String nodeName;

    /**
     * 表单内容
     */
    @TableField(value = "content")
    @Schema(description="表单内容")
    private String content;
}