package org.cloud.demo.workflow.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class FlowSaveXmlDTO implements Serializable {

    /**
     * 流程名称
     */
    @Schema(title = "流程名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 流程分类
     */
    @Schema(title = "流程分类", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    /**
     * xml 文件
     */
    @Schema(title = "xml 文件", requiredMode = Schema.RequiredMode.REQUIRED)
    private String filePath;
}
