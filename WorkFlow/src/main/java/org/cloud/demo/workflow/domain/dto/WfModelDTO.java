package org.cloud.demo.workflow.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.cloud.demo.common.validate.AddGroup;
import org.cloud.demo.common.validate.EditGroup;

@Data
@Schema(name = "模型DTO")
public class WfModelDTO {
    /**
     * 模型主键
     */
    @Schema(description = "模型主键")
    @NotNull(message = "模型主键不能为空", groups = { EditGroup.class })
    private String id;
    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    @NotNull(message = "模型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;
    /**
     * 模型标识
     */
    @Schema(description = "模型标识")
    @NotNull(message = "模型标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private String key;
    /**
     * 流程分类
     */
    @Schema(description = "流程分类")
    @NotBlank(message = "流程分类不能为空", groups = { AddGroup.class, EditGroup.class })
    private String category;
    /**
     * 流程描述
     */
    @Schema(description = "流程描述")
    private String desc;
}
