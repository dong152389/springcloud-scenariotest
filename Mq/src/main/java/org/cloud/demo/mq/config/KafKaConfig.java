package org.cloud.demo.mq.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.cloud.demo.common.constants.TopicConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafKaConfig {
    /**
     * 真实环境肯定不会这么创建，这里进作为一个测试
     * 创建Topic 30 分区 1 副本
     */
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(TopicConstants.PEAK_CLIPPING_TOPIC)
                .partitions(30)
                .replicas(1)
                .compact()
                .build();
    }
}