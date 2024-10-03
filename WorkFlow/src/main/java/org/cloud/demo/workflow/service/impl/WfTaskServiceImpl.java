package org.cloud.demo.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.common.constants.TaskConstants;
import org.cloud.demo.common.enums.FlowComment;
import org.cloud.demo.workflow.service.WfTaskService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WfTaskServiceImpl implements WfTaskService {
    private final TaskService taskService;

    /**
     * 启动第一个任务
     *
     * @param processInstance 流程实例
     * @param variables       流程参数
     */
    @Override
    public void startFirstTask(ProcessInstance processInstance, Map<String, Object> variables) {
        // 查询当前流程实例下的所有任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();

        // 如果存在任务
        if (CollUtil.isNotEmpty(tasks)) {
            // 从流程参数中获取发起人ID
            String userIdStr = (String) variables.get(TaskConstants.PROCESS_INITIATOR);

            // 遍历所有任务
            for (Task task : tasks) {
                // 如果任务的分配人是发起人
                if (StrUtil.equals(task.getAssignee(), userIdStr)) {
                    // 为该任务添加评论
                    taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), "段锋东" + "发起流程申请");
                    // 完成该任务
                    taskService.complete(task.getId(), variables);
                }
            }
        }
    }

}
