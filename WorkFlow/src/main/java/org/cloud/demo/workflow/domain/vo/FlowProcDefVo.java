package org.cloud.demo.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.util.Date;

/**
 * 流程查询列表对象
 */
@Data
@Tag(name = "流程定义")
public class FlowProcDefVo {

    @Schema(description = "流程id")
    private String id;

    @Schema(description = "流程名称")
    private String name;

    @Schema(description = "流程key")
    private String flowKey;

    @Schema(description = "流程分类")
    private String category;

    @Schema(description = "配置表单名称")
    private String formName;

    @Schema(description = "配置表单id")
    private Long formId;

    @Schema(description = "版本")
    private int version;

    @Schema(description = "部署ID")
    private String deploymentId;

    @Schema(description = "流程定义状态: 1:激活 , 2:中止")
    private int suspensionState;

    @Schema(description = "部署时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deploymentTime;

}
