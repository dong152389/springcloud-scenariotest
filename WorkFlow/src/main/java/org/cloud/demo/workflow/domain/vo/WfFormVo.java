package org.cloud.demo.workflow.domain.vo;

import lombok.Data;

@Data
public class WfFormVo {

    /**
     * 表单主键
     */
    private Long id;

    /**
     * 表单名称
     */
    private String name;

    /**
     * 表单内容
     */
    private String content;

    /**
     * 备注
     */
    private String desc;
}