package org.cloud.demo.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表单类型枚举
 *
 * @author DFD
 */
@Getter
@AllArgsConstructor
public enum FormType {

    /**
     * 流程表单
     */
    PROCESS(0),

    /**
     * 外置表单
     */
    EXTERNAL(1),

    /**
     * 节点独立表单
     */
    INDEPENDENT(2);

    /**
     * 表单类型
     */
    private final Integer type;
}
