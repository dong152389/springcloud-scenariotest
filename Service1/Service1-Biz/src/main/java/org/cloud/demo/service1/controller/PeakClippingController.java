package org.cloud.demo.service1.controller;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 削峰测试
 */
@Slf4j
@RestController
@RequestMapping("peakClipping")
public class PeakClippingController {
    private final RedisTemplate<String, String> redisTemplate;

    public PeakClippingController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @GetMapping("getData/{id}")
    public String getData(@PathVariable("id") Long id) {
        String json = redisTemplate.opsForValue().get(id.toString());
        if (StrUtil.isNotBlank(json)) {
            return json;
        }
        //查询数据库，如果查询出来不是null的话，可以再存一次redis
        return "从数据库查询出来的值";
    }
}
