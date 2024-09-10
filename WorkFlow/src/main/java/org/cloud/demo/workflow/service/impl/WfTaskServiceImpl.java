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
        // 若第一个用户任务为发起人，则自动完成任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();
        if (CollUtil.isNotEmpty(tasks)) {
            String userIdStr = (String) variables.get(TaskConstants.PROCESS_INITIATOR);
            for (Task task : tasks) {
                if (StrUtil.equals(task.getAssignee(), userIdStr)) {
                    taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), "段锋东" + "发起流程申请");
                    taskService.complete(task.getId(), variables);
                }
            }
        }
    }
}
