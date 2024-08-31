package org.cloud.demo.common.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义拒绝策略
 */
@Component
@RefreshScope
public class CustomRejectedExecution implements RejectedExecutionHandler {

    // 重试的最大次数
    @Value("${dynamic.threadpool.maxRetry}")
    private int retryMaxCount;


    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            RetryableTask task = (RetryableTask) r;
            if (task.addAndGet() <= retryMaxCount) {
                // 重新提交任务
                executor.execute(task);
            } else {
                sendSmsNotification(task.getTaskId());
            }
        } catch (Exception e) {
            // 达到最大重试次数,发送短信通知
            sendSmsNotification(r);
        }
    }

    private void sendSmsNotification(String taskId) {
        // 这里实现发送短信的逻辑
        System.out.println("发送短信通知: 任务 " + taskId + " 已被拒绝超过" + retryMaxCount + "次");
    }

    private void sendSmsNotification(Runnable r) {
        // 这里实现发送短信的逻辑
        System.out.println("发送短信通知: 任务已被拒绝超过" + retryMaxCount + "次");
    }
}

