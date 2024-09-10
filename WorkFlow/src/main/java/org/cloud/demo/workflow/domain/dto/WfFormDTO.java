package org.cloud.demo.workflow.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.cloud.demo.common.validate.AddGroup;
import org.cloud.demo.common.validate.EditGroup;

@Data
@Schema(name = "表单DTO")
public class WfFormDTO {

    /**
     * 表单主键
     */
    @NotNull(message = "表单ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 表单名称
     */
    @NotBlank(message = "表单名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 表单内容
     */
    @NotBlank(message = "表单内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;

    /**
     * 备注
     */
    private String desc;
}
