package org.cloud.demo.service1.controller;

import jakarta.annotation.Resource;
import org.cloud.demo.common.threadpool.DynamicThreadPool;
import org.cloud.demo.common.threadpool.RetryableTask;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试动态线程池
 */
@RestController
@RequestMapping("tp")
public class ThreadPoolController {
    @Resource
    private DynamicThreadPool dynamicThreadPool;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @GetMapping("/getTreadPoolStatus")
    public String getTreadPoolStatus() {
        return dynamicThreadPool.checkPoolStatus(threadPoolTaskExecutor);
    }

    @GetMapping("/execute")
    public void execute() {
        for (int i = 0; i < 2000; i++) {
            final int taskId = i;
            RetryableTask retryableTask = new RetryableTask(taskId + "", () -> {
                System.out.println("任务：" + taskId + "开始运行");
                try {
                    // 任务运行5秒，以确保线程池被填满
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("任务：" + taskId + "运行完成");
            });
            threadPoolTaskExecutor.execute(retryableTask);
        }
    }
}
