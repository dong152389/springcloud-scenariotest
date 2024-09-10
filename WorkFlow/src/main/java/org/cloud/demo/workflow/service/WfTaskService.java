package org.cloud.demo.workflow.service;

import org.flowable.engine.runtime.ProcessInstance;

import java.util.Map;

public interface WfTaskService {
    void startFirstTask(ProcessInstance processInstance, Map<String, Object> variables);
}
