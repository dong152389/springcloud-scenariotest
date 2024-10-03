package org.cloud.demo.workflow.domain.vo;

import lombok.Data;

import java.util.Map;

/**
 * 发起流程的表单VO
 */
@Data
public class ProcessFormVo {
    /**
     * 标题
     */
    private String title;

    /**
     * 禁用表单
     */
    private Boolean disabled = false;
    /**
     * 表单按钮
     */
    private Boolean formBtns = true;

    /**
     * 表单模型
     */
    private Map<String, Object> formModel;

    /**
     * 表单数据
     */
    private Map<String, Object> formData;
}
