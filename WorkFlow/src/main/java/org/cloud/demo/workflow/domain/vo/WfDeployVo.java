package org.cloud.demo.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class WfDeployVo {
    // 流程ID
    private String processDefId;
    // 流程名称
    private String processDefName;
    private String processDefKey;
    // 分类名称
    private String category;
    // 版本
    private Integer version;
    // 状态是否为挂起
    private Boolean suspended;
    private String deployId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date deployTime;
}
