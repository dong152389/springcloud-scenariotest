package org.cloud.demo.workflow.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "部署模型DTO")
public class DeployModelDTO {
    @NotBlank(message = "模型ID不能为空!")
    @Schema(description = "模型ID")
    private String modelId;
    @Schema(description = "部署模型权限-用户")
    private List<String> deployUsers;
    @Schema(description = "部署模型权限-组")
    private List<String> deployGroups;
}
