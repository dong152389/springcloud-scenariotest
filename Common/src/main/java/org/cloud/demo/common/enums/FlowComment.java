package org.cloud.demo.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 流程意见类型
 */
@Getter
@RequiredArgsConstructor
public enum FlowComment {

    /**
     * 说明
     */
    NORMAL("1", "正常"),
    REBACK("2", "退回"),
    REJECT("3", "驳回"),
    DELEGATE("4", "委派"),
    TRANSFER("5", "转办"),
    STOP("6", "终止"),
    REVOKE("7", "撤回");

    /**
     * 类型
     */
    private final String type;

    /**
     * 说明
     */
    private final String remark;
}
