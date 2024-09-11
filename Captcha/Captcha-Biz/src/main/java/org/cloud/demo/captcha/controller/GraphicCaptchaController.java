package org.cloud.demo.captcha.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.captcha.service.CaptchaService;
import org.cloud.demo.common.web.BaseController;
import org.cloud.demo.common.web.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码操作处理
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class GraphicCaptchaController extends BaseController {
    private final CaptchaService captchaService;

    /**
     * 生成图形验证码
     */
    @GetMapping("/captchaImage")
    public R<String> captchaImage() {
        String captcha = captchaService.captchaImage();
        return R.ok(captcha);
    }

}
