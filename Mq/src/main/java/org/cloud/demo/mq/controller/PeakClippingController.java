package org.cloud.demo.mq.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.common.constants.TopicConstants;
import org.cloud.demo.service1.domain.DemoValueBo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 削峰测试
 */
@Slf4j
@RestController
@RequestMapping("peakClipping")
public class PeakClippingController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PeakClippingController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 供给第三方系统调用，模拟一个保存操作
     *
     * @param jsonData
     */
    @PostMapping("saveData")
    public void saveData(@RequestBody DemoValueBo jsonData) {
        // 这里实现削峰的逻辑
        CompletableFuture<SendResult<String, Object>> completableFuture = kafkaTemplate.send(TopicConstants.PEAK_CLIPPING_TOPIC, IdUtil.getSnowflakeNextIdStr(), JSONUtil.toJsonStr(jsonData));
        completableFuture.thenAccept(sendResult -> {
            // 处理成功发送的结果
            log.info("发送成功，消息ID:{}", sendResult.getProducerRecord().key());
        }).exceptionally(throwable -> {
            // 处理发送失败的情况
            log.error("发送失败{}", throwable.getMessage());
            return null;
        });
    }
}
