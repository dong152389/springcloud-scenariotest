package org.cloud.demo.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cloud.demo.common.constants.ProcessConstants;
import org.cloud.demo.common.enums.ProcessStatus;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.exception.ServiceException;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.WfDeployForm;
import org.cloud.demo.workflow.domain.dto.ProcessQuery;
import org.cloud.demo.workflow.domain.vo.ProcessFormVo;
import org.cloud.demo.workflow.domain.vo.WfDefinitionVo;
import org.cloud.demo.workflow.mapper.WfDeployFormMapper;
import org.cloud.demo.workflow.service.WfProcessService;
import org.cloud.demo.workflow.service.WfTaskService;
import org.cloud.demo.workflow.utils.ModelUtils;
import org.cloud.demo.workflow.utils.ProcessUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WfProcessServiceImpl implements WfProcessService {

    private final RepositoryService repositoryService;
    private final HistoryService historyService;
    private final IdentityService identityService;
    private final RuntimeService runtimeService;
    private final WfTaskService wfTaskService;
    private final WfDeployFormMapper wfDeployFormMapper;


    @Override
    public ProcessFormVo selectFormContent(String definitionId, String deployId, String procInsId) {
        ProcessFormVo processFormVo = new ProcessFormVo();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        if (ObjectUtil.isNull(bpmnModel)) throw new ServiceException("流程图设计为空！");
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) throw new ServiceException("流程图中没有开始节点，请检查流程设计！");
        WfDeployForm wfDeployForm = wfDeployFormMapper.selectOne(new LambdaQueryWrapper<>(WfDeployForm.class)
                .eq(WfDeployForm::getDeployId, deployId)
                .eq(WfDeployForm::getFormKey, startEvent.getFormKey())
                .eq(WfDeployForm::getNodeKey, startEvent.getId()));
        if (ObjectUtil.isNull(wfDeployForm)) throw new ServiceException("流程表单为空！");
        String formContent = wfDeployForm.getContent();
        if (StrUtil.isBlank(formContent)) throw new ServiceException("获取流程表单失败！");
        Map<String, Object> map = JSON.parseObject(formContent, Map.class);
        processFormVo.setFormModel(map);
        // 这个字段第一次发起并不能携带
        if (StrUtil.isNotBlank(procInsId)) {
            // 获取流程实例
            HistoricProcessInstance historicProcIns = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInsId)
                    .includeProcessVariables()
                    .singleResult();
            processFormVo.setFormData(historicProcIns.getProcessVariables());
        }
        return processFormVo;
    }

    @Override
    public void startProcessByDefId(String processDefId, Map<String, Object> variables) {
        // 比如这里查询出来用户的id和组
        String userId = "123L";
        String groupId = "321";
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefId)
                .startableByUserOrGroups(userId, Collections.singleton(groupId))
                .singleResult();
        if (ObjectUtil.isNull(processDefinition)) {
            throw new ServiceException("流程已被挂起，请先激活流程");
        }
        // 设置流程发起人ID
        identityService.setAuthenticatedUserId(userId);
        variables.put(BpmnXMLConstants.ATTRIBUTE_EVENT_START_INITIATOR, userId);
        // 设置流程状态为进行中
        variables.put(ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.RUNNING.getStatus());
        // 发起流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
        // 第一个用户任务为发起人，则自动完成任务
        wfTaskService.startFirstTask(processInstance, variables);
    }

    @Override
    public TableDataInfo<WfDefinitionVo> selectPageStartProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        // 创建查询对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .latestVersion()                //最新版本
                .active()                       //活跃状态
                .orderByProcessDefinitionKey()  //根据标识排序
                .desc();//倒叙

        // 构建查询参数
        ProcessUtils.buildProcessSearch(processDefinitionQuery, processQuery);
        long pageTotal = processDefinitionQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        int pageSize = pageQuery.getPageSize();
        int offset = pageSize * (pageQuery.getPageNum() - 1);
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.listPage(offset, pageSize);
        // 查询部署时间
        Map<String, Date> deployMap = new HashMap<>();
        List<Deployment> deployments = repositoryService.createDeploymentQuery().deploymentIds(processDefinitions.stream().map(ProcessDefinition::getDeploymentId).collect(Collectors.toList())).list();
        if (CollectionUtil.isNotEmpty(deployments)) {
            deployMap = CollUtil.toMap(deployments, deployMap, Deployment::getId, Deployment::getDeploymentTime);
        }
        // 封装对象
        List<WfDefinitionVo> wfDefinitionVos = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitions) {
            WfDefinitionVo vo = new WfDefinitionVo();
            vo.setProcessDefinitionId(processDefinition.getId());
            vo.setProcessDefinitionKey(processDefinition.getKey());
            vo.setProcessDefinitionName(processDefinition.getName());
            vo.setVersion(processDefinition.getVersion());
            vo.setCategory(processDefinition.getCategory());
            vo.setSuspended(processDefinition.isSuspended());
            vo.setDeploymentTime(deployMap.get(processDefinition.getDeploymentId()));
            wfDefinitionVos.add(vo);
        }
        Page<WfDefinitionVo> page = new Page<>();
        page.setTotal(pageTotal);
        page.setRecords(wfDefinitionVos);
        return TableDataInfo.build(page);
    }
}
