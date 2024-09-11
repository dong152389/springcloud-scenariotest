package org.cloud.demo.captcha.service.impl;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.captcha.enums.CaptchaType;
import org.cloud.demo.captcha.properties.GraphicCaptchaProperties;
import org.cloud.demo.captcha.service.CaptchaService;
import org.cloud.demo.common.constants.CacheConstants;
import org.cloud.demo.common.constants.CaptchaConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final GraphicCaptchaProperties graphicCaptchaProperties;
    private final RedisTemplate redisTemplate;

    @Override
    public String captchaImage() {
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
        // 生成验证码
        CaptchaType captchaType = graphicCaptchaProperties.getType();
        // 生成验证码
        boolean isMath = CaptchaType.MATH == captchaType;
        Integer length = isMath ? graphicCaptchaProperties.getNumberLength() : graphicCaptchaProperties.getCharLength();
        CodeGenerator codeGenerator = ReflectUtil.newInstance(captchaType.getClazz(), length);
        AbstractCaptcha captcha = SpringUtil.getBean(graphicCaptchaProperties.getCategory().getClazz());
        captcha.setGenerator(codeGenerator);
        captcha.createCode();
        String code = captcha.getCode();
        if (isMath) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(StrUtil.removeAll(code, "="));
            code = exp.getValue(String.class);
        }
        redisTemplate.opsForValue().set(verifyKey, code);
        redisTemplate.expire(verifyKey, Duration.ofMinutes(CaptchaConstants.CAPTCHA_EXPIRATION));
        return captcha.getImageBase64();
    }
}
