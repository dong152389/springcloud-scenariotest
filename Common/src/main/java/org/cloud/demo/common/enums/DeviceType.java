package org.cloud.demo.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 设备类型
 * 针对一套 用户体系
 */
@Getter
@RequiredArgsConstructor
public enum DeviceType {

    /**
     * pc端
     */
    PC("pc"),

    /**
     * app端
     */
    APP("app"),

    /**
     * 小程序端
     */
    XCX("xcx");

    private final String device;
}
