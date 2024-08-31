package org.cloud.demo.common.threadpool;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

// 可重试的任务包装类
public class RetryableTask implements Runnable {
    private static final AtomicInteger retryCount = new AtomicInteger(0);
    @Getter
    private final String taskId;
    private final Runnable task;

    public RetryableTask(String taskId, Runnable task) {
        this.taskId = taskId;
        this.task = task;
    }

    @Override
    public void run() {
        task.run();
    }

    public int addAndGet() {
        return retryCount.addAndGet(1);
    }

}
