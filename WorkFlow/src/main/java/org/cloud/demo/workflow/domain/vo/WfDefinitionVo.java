package org.cloud.demo.workflow.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class WfDefinitionVo {
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private String category;
    private Integer version;
    /**
     * 流程是否暂停（true:挂起 false:激活 ）
     */
    private Boolean suspended;
    private Date deploymentTime;
}