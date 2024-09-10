package org.cloud.demo.service1.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.cloud.demo.common.constants.TopicConstants;
import org.cloud.demo.service1.domain.DemoValueBo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PeakClippingListener {
    private final RedisTemplate<String, String> redisTemplate;

    public PeakClippingListener(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 可以收到之前发的消息
     */
    @KafkaListener(id = "1", groupId = "pcg-1", topicPartitions = {@TopicPartition(topic = TopicConstants.PEAK_CLIPPING_TOPIC, partitions = "0-29")})
    public void listenAll(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("收到:{}，消息为:{}", record.partition(), record);
        String value = record.value();
        DemoValueBo demoValueBo = JSONUtil.toBean(value, DemoValueBo.class);
        if (ObjectUtil.isNull(demoValueBo)) {
            ack.acknowledge();
            throw new RuntimeException("当前消息有误");
        }
        //存到Redis
        redisTemplate.opsForValue().set(demoValueBo.getId().toString(), demoValueBo.getData());
        //存到Mysql
        log.info("保存到了Mysql中");
        ack.acknowledge();
    }


}
