package org.cloud.demo.workflow.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.cloud.demo.common.validate.AddGroup;
import org.cloud.demo.common.validate.EditGroup;

@Data
@Schema(name = "流程分类DTO")
public class WfCategoryDTO {
    @NotNull(message = "流程分类ID不能为空", groups = {EditGroup.class})
    @Schema(description = "流程分类ID")
    private Long id;

    @NotBlank(message = "流程分类名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "流程分类名称")
    private String name;

    @Schema(description = "流程分类描述")
    private String desc;
}
