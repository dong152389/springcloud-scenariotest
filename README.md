# 对工作中一些方案的简单场景测试

- Service1-> Nacos监听机制 + ThreadPoolTaskExecutor 完成动态线程池
- MQ、Service1-> 利用Kafka消息队列完成流量削峰
- Service1调用Service2-> 服务的熔断和降级