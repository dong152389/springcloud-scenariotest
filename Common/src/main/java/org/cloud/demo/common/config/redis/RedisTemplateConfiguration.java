//package org.cloud.demo.common.config.redis;
//
//import org.springframework.boot.autoconfigure.AutoConfiguration;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.*;
//import org.springframework.data.redis.serializer.RedisSerializer;
//
///**
// * Redis 配置类
// */
//@EnableCaching
//@AutoConfiguration
//@AutoConfigureBefore(RedisAutoConfiguration.class)
//public class RedisTemplateConfiguration {
//
//	@Bean
//	@Primary
//	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//		redisTemplate.setKeySerializer(RedisSerializer.json());
//		redisTemplate.setHashKeySerializer(RedisSerializer.json());
//		redisTemplate.setValueSerializer(RedisSerializer.json());
//		redisTemplate.setHashValueSerializer(RedisSerializer.json());
//		redisTemplate.setConnectionFactory(factory);
//		return redisTemplate;
//	}
//
//}