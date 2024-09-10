package org.cloud.demo.workflow.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.cloud.demo.common.validate.AddGroup;
import org.cloud.demo.common.validate.EditGroup;

@Data
@Schema(name = "模型流程图DTO")
public class WfBpmnModelDTO {

    /**
     * 流程ID
     */
    @Schema(description = "流程模型ID")
    private String modeId;

    /**
     * 流程设计图xml
     */
    @Schema(description = "流程设计图xml")
    @NotBlank(message = "流程设计图必须要设计，不能空保存！", groups = {AddGroup.class, EditGroup.class})
    private String bpmnXml;

    /**
     * 是否是最新版本
     */
    @NotNull(message = "必须确认是否保存为最新版本", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "是否是最新版本")
    private Boolean newVersion;
}
