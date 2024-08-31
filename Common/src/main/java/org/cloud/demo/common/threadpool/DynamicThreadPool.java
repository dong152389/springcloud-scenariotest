package org.cloud.demo.common.threadpool;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 改类继承自ThreadPoolExecutor，用于实现自定义线程池
 *
 * @author DFD
 */

@Configuration
@RefreshScope
@Slf4j
public class DynamicThreadPool implements InitializingBean {
    private final static String dataId = "common.yaml";
    private static ThreadPoolTaskExecutor threadPoolExecutor;
    //拒绝的任务数量
    private final NacosConfigManager nacosConfigManager;
    private final NacosConfigProperties nacosConfigProperties;
    private final CustomRejectedExecution customRejectedExecution;
    @Value("${dynamic.threadpool.coreSize}")
    private int coreSize;
    @Value("${dynamic.threadpool.maxSize}")
    private int maxSize;
    @Value("${dynamic.threadpool.maxCapacity}")
    private int maxCapacity;


    public DynamicThreadPool(NacosConfigManager nacosConfigManager, NacosConfigProperties nacosConfigProperties, CustomRejectedExecution customRejectedExecution) {
        this.nacosConfigManager = nacosConfigManager;
        this.nacosConfigProperties = nacosConfigProperties;
        this.customRejectedExecution = customRejectedExecution;
    }

    /**
     * 创建RejectedExecutionHandler代理
     *
     * @param rejectedExecutionHandler 拒绝策略
     * @param rejectedNum              拒绝任务数量
     * @return 代理后的拒绝策略
     */
//    public static RejectedExecutionHandler createProxy(RejectedExecutionHandler rejectedExecutionHandler, AtomicLong rejectedNum) {
//        return (RejectedExecutionHandler) Proxy.newProxyInstance(rejectedExecutionHandler.getClass().getClassLoader(), new Class[]{RejectedExecutionHandler.class}, new RejectedProxyInvocationHandler(rejectedExecutionHandler, rejectedNum));
//    }
    @Override
    public void afterPropertiesSet() throws NacosException {
        createThreadPool(coreSize, maxSize, maxCapacity);

        nacosConfigManager.getConfigService().addListener(dataId, nacosConfigProperties.getGroup(), new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("============ 刷新线程池参数 ============");

                Yaml yaml = new Yaml();
                Map<String, Object> config = yaml.load(configInfo);

                if (config == null) {
                    log.warn("配置为空，无法刷新线程池参数");
                    return;
                }

                Map<String, Object> customConfigMap = (Map<String, Object>) config.getOrDefault("dynamic", new HashMap<>());
                Map<String, Object> threadPoolConfigMap = (Map<String, Object>) customConfigMap.getOrDefault("threadpool", new HashMap<>());

                if (threadPoolConfigMap.isEmpty()) {
                    log.warn("线程池配置为空，无法刷新线程池参数");
                    return;
                }

                coreSize = Optional.ofNullable((Integer) threadPoolConfigMap.get("coreSize")).orElse(coreSize);
                maxSize = Optional.ofNullable((Integer) threadPoolConfigMap.get("maxSize")).orElse(maxSize);
                maxCapacity = Optional.ofNullable((Integer) threadPoolConfigMap.get("maxCapacity")).orElse(maxCapacity);

                log.info("============ 线程池配置：{} ============", JSONUtil.toJsonStr(threadPoolConfigMap));
                dynamicRefresh(coreSize, maxSize, maxCapacity);
            }
        });
    }

    private void createThreadPool(int coreSize, int maxSize, int maxCapacity) {
        threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setCorePoolSize(coreSize);
        threadPoolExecutor.setMaxPoolSize(maxSize);
        threadPoolExecutor.setThreadNamePrefix("dfd_tp-");
        threadPoolExecutor.setQueueCapacity(maxCapacity);
        threadPoolExecutor.setRejectedExecutionHandler(customRejectedExecution);
    }

    private void dynamicRefresh(int coreSize, int maxSize, int maxCapacity) {
        //这里需要重新创建一份，原因就在于在threadPoolTaskExecutor中设置核心和最大的时候会有个判断，极有可能发生原最大数小于现核心数
        createThreadPool(coreSize, maxSize, maxCapacity);
        log.info("线程池数量已经动态改变，核心线程数量:{}，最大线程数量:{}，最大队列容量:{}", threadPoolExecutor.getCorePoolSize(), threadPoolExecutor.getMaxPoolSize(), threadPoolExecutor.getQueueCapacity());
    }

    /**
     * 获取线程池
     *
     * @return
     */
    @Bean
    @Primary
    public ThreadPoolTaskExecutor newInstance() {
        return threadPoolExecutor;
    }

    /**
     * 查看线程池的状态
     *
     * @return
     */
    public String checkPoolStatus(ThreadPoolTaskExecutor threadPoolExecutor) {
        String msg = "核心线程数为：" + threadPoolExecutor.getCorePoolSize() + "最大线程数为：" + threadPoolExecutor.getMaxPoolSize();
        log.info("核心线程数量:{}，最大线程数量:{}", threadPoolExecutor.getCorePoolSize() + "", threadPoolExecutor.getMaxPoolSize() + "");
        return msg;
    }

    /**
     * 线程工厂
     */
    private static class CustomTF implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "myThreadPool-" + threadNumber.getAndIncrement());
        }
    }
}
