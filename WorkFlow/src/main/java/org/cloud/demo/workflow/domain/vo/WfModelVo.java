package org.cloud.demo.workflow.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class WfModelVo {
    private String modelId;
    private String modelName;
    private String modelKey;
    private String category;
    private Integer version;
    private Long formId;
    private String formContent;
    private String description;
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String bpmnXml;
}
