package org.cloud.demo.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.vo.RoleVo;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.cloud.demo.common.constants.ProcessConstants;
import org.cloud.demo.common.constants.TaskConstants;
import org.cloud.demo.common.enums.ProcessStatus;
import org.cloud.demo.common.utils.LoginUtils;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.exception.ServiceException;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.WfDeployForm;
import org.cloud.demo.workflow.domain.dto.ProcessQuery;
import org.cloud.demo.workflow.domain.vo.*;
import org.cloud.demo.workflow.feign.AuthFeignClient;
import org.cloud.demo.workflow.mapper.WfDeployFormMapper;
import org.cloud.demo.workflow.service.WfProcessService;
import org.cloud.demo.workflow.service.WfTaskService;
import org.cloud.demo.workflow.utils.ModelUtils;
import org.cloud.demo.workflow.utils.ProcessUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WfProcessServiceImpl implements WfProcessService {

    private final RepositoryService repositoryService;
    private final HistoryService historyService;
    private final IdentityService identityService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final WfTaskService wfTaskService;
    private final WfDeployFormMapper wfDeployFormMapper;
    private final AuthFeignClient authFeignClient;


    /**
     * 根据流程定义ID、部署ID和流程实例ID查询对应的流程表单内容
     *
     * @param definitionId 流程定义ID
     * @param deployId     部署ID
     * @param procInsId    流程实例ID
     * @return 流程表单对象
     */
    @Override
    public ProcessFormVo selectFormContent(String definitionId, String deployId, String procInsId) {
        // 初始化流程表单对象
        ProcessFormVo processFormVo = new ProcessFormVo();
        // 根据流程定义ID获取BPMN模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        // 如果BPMN模型为空，则抛出异常
        if (ObjectUtil.isNull(bpmnModel)) throw new ServiceException("流程图设计为空！");
        // 从BPMN模型中获取开始事件
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        // 如果开始事件为空，则抛出异常
        if (ObjectUtil.isNull(startEvent)) throw new ServiceException("流程图中没有开始节点，请检查流程设计！");
        // 根据部署ID和开始事件的信息查询对应的流程表单
        WfDeployForm wfDeployForm = wfDeployFormMapper.selectOne(new LambdaQueryWrapper<>(WfDeployForm.class)
                .eq(WfDeployForm::getDeployId, deployId)
                .eq(WfDeployForm::getFormKey, startEvent.getFormKey())
                .eq(WfDeployForm::getNodeKey, startEvent.getId()));
        // 如果查询到的流程表单为空，则抛出异常
        if (ObjectUtil.isNull(wfDeployForm)) throw new ServiceException("流程表单为空！");
        // 获取流程表单的内容
        String formContent = wfDeployForm.getContent();
        // 如果表单内容为空或仅包含空白字符，则抛出异常
        if (StrUtil.isBlank(formContent)) throw new ServiceException("获取流程表单失败！");
        // 将表单内容解析为Map对象
        Map<String, Object> formModel = JSON.parseObject(formContent, Map.class);
        // 设置表单按钮的显示状态为不显示
        processFormVo.setFormBtns(false);
        // 设置表单模型
        processFormVo.setFormModel(formModel);
        // 如果流程实例ID不为空，则执行以下逻辑
        if (StrUtil.isNotBlank(procInsId)) {
            // 根据流程实例ID查询历史流程实例信息，并包含流程变量
            HistoricProcessInstance historicProcIns = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInsId)
                    .includeProcessVariables()
                    .singleResult();
            // 设置流程表单的数据为历史流程实例中的流程变量
            processFormVo.setFormData(historicProcIns.getProcessVariables());
        }
        // 返回流程表单对象
        return processFormVo;
    }


    /**
     * 根据流程定义ID启动流程实例
     *
     * @param processDefId 流程定义ID
     * @param variables    流程变量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startProcessByDefId(String processDefId, Map<String, Object> variables) {
        // 查询流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefId)
                .singleResult();
        // 如果流程定义不存在，抛出异常
        if (ObjectUtil.isNull(processDefinition)) {
            throw new ServiceException("流程已被挂起，请先激活流程");
        }
        // 设置流程发起人ID,应该从数据库查询
        String userId = "1L";
        identityService.setAuthenticatedUserId(userId);
        // 将发起人ID作为流程变量
        variables.put(BpmnXMLConstants.ATTRIBUTE_EVENT_START_INITIATOR, userId);
        // 设置流程状态为进行中
        variables.put(ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.RUNNING.getStatus());
        // 发起流程实例，并传入流程变量
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
        // 如果第一个用户任务是发起人，则自动完成该任务
        wfTaskService.startFirstTask(processInstance, variables);
    }


    /**
     * 查询流程定义列表
     *
     * @param processQuery 流程查询对象
     * @param pageQuery    分页查询对象
     * @return 流程定义列表
     */
    @Override
    public TableDataInfo<WfDefinitionVo> selectPageStartProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        String userId = LoginUtils.getLoginUser().getUserId().toString();
        List<String> roleIds = LoginUtils.getRoleIds();
        // 创建查询对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .latestVersion()                //最新版本
                .active()                       //活跃状态
                .orderByProcessDefinitionKey()  //根据标识排序
                .startableByUserOrGroups(userId, roleIds)  //权限
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

    /**
     * 获取当前用户的发起的流程列表
     *
     * @param processQuery 查询条件
     * @param pageQuery    分页查询条件
     * @return 发起的流程列表
     */
    @Override
    public TableDataInfo<WfMyProcessVo> selectPageOwnProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        String userId = "1L";

        // 查询由特定用户启动的所有流程实例，并按启动时间降序排列
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId)
                .orderByProcessInstanceStartTime()
                .desc();

        // 构建查询条件
        ProcessUtils.buildProcessSearch(historicProcessInstanceQuery, processQuery);

        // 分页条件
        long pageTotal = historicProcessInstanceQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }

        int pageSize = pageQuery.getPageSize();
        int offset = pageSize * (pageQuery.getPageNum() - 1);

        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(offset, pageSize);
        List<WfMyProcessVo> taskVoList = new ArrayList<>(historicProcessInstances.size());

        // 批量获取流程状态和分类，避免重复查询
        Map<String, String> processStatusMap = getProcessStatusMap(historicProcessInstances);
        Map<String, String> categoryMap = getCategoryMap(historicProcessInstances);

        for (HistoricProcessInstance processInstance : historicProcessInstances) {
            WfMyProcessVo vo = new WfMyProcessVo();
            vo.setProcDefId(processInstance.getProcessDefinitionId());
            vo.setProcDefKey(processInstance.getProcessDefinitionKey());
            vo.setProcDefName(processInstance.getProcessDefinitionName());
            vo.setProcDefVersion(processInstance.getProcessDefinitionVersion());
            vo.setProcInsId(processInstance.getId());

            // 设置流程状态
            String processStatus = processStatusMap.getOrDefault(processInstance.getId(),
                    ObjectUtil.isNull(processInstance.getEndTime()) ? ProcessStatus.RUNNING.getStatus() : ProcessStatus.COMPLETED.getStatus());
            vo.setProcessStatus(processStatus);

            // 设置流程分类
            vo.setCategory(categoryMap.get(processInstance.getDeploymentId()));

            vo.setSubmitTime(processInstance.getStartTime());
            vo.setFinishTime(processInstance.getEndTime());

            // 获取当前节点
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeIdentityLinks().list();
            String taskNames = tasks.stream()
                    .map(TaskInfo::getName)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.joining(","));
            vo.setCurrentNode(taskNames);

            taskVoList.add(vo);
        }
        Page<WfMyProcessVo> page = new Page<>();
        page.setTotal(pageTotal);
        page.setRecords(taskVoList);
        return TableDataInfo.build(page);
    }

    /**
     * 查询流程详情信息
     *
     * @param procInsId 流程实例ID
     * @param taskId    任务ID
     */
    @Override
    public WfProcessDetailVo queryProcessDetail(String procInsId, String taskId) {
        WfProcessDetailVo wfProcessDetailVo = new WfProcessDetailVo();
        // 获取流程实例
        HistoricProcessInstance hisProcIns = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(procInsId)
                .includeProcessVariables() //包括流程变量
                .singleResult();
        if (StrUtil.isNotBlank(taskId)) {
            HistoricTaskInstance hisTaskIns = historyService.createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .includeIdentityLinks()
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .singleResult();
            if (ObjectUtil.isNull(hisTaskIns)) {
                throw new ServiceException("没有可办理的任务！");
            }
            wfProcessDetailVo.setProcessFormVo(currTaskFormData(hisProcIns.getDeploymentId(), hisTaskIns));
        }
        // 获取Bpmn模型信息
        InputStream inputStream = repositoryService.getProcessModel(hisProcIns.getProcessDefinitionId());
        String bpmnXmlStr = StrUtil.utf8Str(IoUtil.readBytes(inputStream, false));
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXmlStr);
        wfProcessDetailVo.setBpmnXml(bpmnXmlStr);
        wfProcessDetailVo.setHistoryProcNodeList(historyProcNodeList(hisProcIns));
//        wfProcessDetailVo.setProcessFormList(processFormList(bpmnModel, historicProcIns));
//        wfProcessDetailVo.setFlowViewer(getFlowViewer(bpmnModel, procInsId));
        return wfProcessDetailVo;
    }

    private List<WfProcNodeVo> historyProcNodeList(HistoricProcessInstance hisProcIns) {
        String procInsId = hisProcIns.getId();
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInsId)
                .activityTypes(CollUtil.newHashSet(BpmnXMLConstants.ELEMENT_EVENT_START, BpmnXMLConstants.ELEMENT_EVENT_END, BpmnXMLConstants.ELEMENT_TASK_USER))
                .orderByHistoricActivityInstanceStartTime().desc()
                .orderByHistoricActivityInstanceEndTime().desc()
                .list();
        List<Comment> comments = taskService.getProcessInstanceComments(procInsId);

        List<WfProcNodeVo> elementVoList = new ArrayList<>();
        for (HistoricActivityInstance activityInstance : historicActivityInstances) {
            WfProcNodeVo elementVo = new WfProcNodeVo();
            elementVo.setActivityId(activityInstance.getActivityId());
            elementVo.setActivityName(activityInstance.getActivityName());
            elementVo.setProcDefId(activityInstance.getProcessDefinitionId());
            elementVo.setActivityType(activityInstance.getActivityType());
            elementVo.setCreateTime(activityInstance.getStartTime());
            elementVo.setEndTime(activityInstance.getEndTime());
            if (ObjectUtil.isNotNull(activityInstance.getDurationInMillis())) {
                elementVo.setDuration(DateUtil.formatBetween(activityInstance.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            }
            if (BpmnXMLConstants.ELEMENT_EVENT_START.equals(activityInstance.getActivityType())) {
                if (ObjectUtil.isNotNull(hisProcIns)) {
                    Long userId = Long.valueOf(hisProcIns.getStartUserId());
                    UserVo userInfo = authFeignClient.getUserInfo(userId);
                    if (ObjectUtil.isNotNull(userInfo)) {
                        elementVo.setAssigneeId(userId);
                        elementVo.setAssigneeName(userInfo.getUserName() + "-" + userInfo.getNickName());
                    }
                }
            } else if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(activityInstance.getActivityType())) {
                //用户ID
                if (StrUtil.isNotBlank(activityInstance.getAssignee())) {
                    Long userId = Long.valueOf(hisProcIns.getStartUserId());
                    UserVo userInfo = authFeignClient.getUserInfo(userId);
                    if (ObjectUtil.isNotNull(userInfo)) {
                        elementVo.setAssigneeId(userId);
                        elementVo.setAssigneeName(userInfo.getUserName() + "-" + userInfo.getNickName());
                    }
                }
                // 展示审批人员
                List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(activityInstance.getTaskId());
                StringBuilder stringBuilder = new StringBuilder();
                for (HistoricIdentityLink identityLink : linksForTask) {
                    if ("candidate".equals(identityLink.getType())) {
                        String userIdStr = identityLink.getUserId();
                        if (StrUtil.isNotBlank(userIdStr)) {
                            Long userId = Long.valueOf(userIdStr);
                            UserVo userInfo = authFeignClient.getUserInfo(userId);
                            String approveName = userInfo.getNickName() + "-" + userInfo.getUserName();
                            stringBuilder.append(approveName).append(",");
                        }
                        String groupIdStr = identityLink.getGroupId();
                        if (StrUtil.isNotBlank(groupIdStr)) {
                            if (groupIdStr.startsWith(TaskConstants.ROLE_GROUP_PREFIX)) {
                                Long roleId = Long.parseLong(StrUtil.removePrefix(groupIdStr, TaskConstants.ROLE_GROUP_PREFIX));
                                RoleVo roleVo = authFeignClient.queryRoleByRoleId(roleId);
                                if (ObjectUtil.isNotNull(roleVo)) {
                                    stringBuilder.append(roleVo.getRoleName()).append(",");
                                }
                            }
//                            else if (groupIdStr.startsWith(TaskConstants.DEPT_GROUP_PREFIX)) {
//                                Long deptId = Long.parseLong(StrUtil.stripStart(identityLink.getGroupId(), TaskConstants.DEPT_GROUP_PREFIX));
//                                SysDept dept = deptService.selectDeptById(deptId);
//                                stringBuilder.append(dept.getDeptName()).append(",");
//                            }
                        }
                    }
                }
                if (StrUtil.isNotBlank(stringBuilder)) {
                    elementVo.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                }
                // 获取意见评论内容
                if (CollUtil.isNotEmpty(comments)) {
                    List<Comment> commentList = new ArrayList<>();
                    for (Comment comment : comments) {

                        if (comment.getTaskId().equals(activityInstance.getTaskId())) {
                            commentList.add(comment);
                        }
                    }
                    elementVo.setCommentList(commentList);
                }
            }
            elementVoList.add(elementVo);
        }
        return elementVoList;
    }


    /**
     * 获取当前任务表单数据
     *
     * @param deploymentId 流程部署ID
     * @param hisTaskIns   历史任务实例
     * @return 返回当前任务表单数据
     */
    private ProcessFormVo currTaskFormData(String deploymentId, HistoricTaskInstance hisTaskIns) {
        WfDeployFormVo wfDeployFormVo = wfDeployFormMapper.selectVoOne(new LambdaQueryWrapper<WfDeployForm>()
                .eq(WfDeployForm::getDeployId, deploymentId)
                .eq(WfDeployForm::getFormKey, hisTaskIns.getFormKey())
                .eq(WfDeployForm::getNodeKey, hisTaskIns.getTaskDefinitionKey()));
        if (ObjectUtil.isNotNull(wfDeployFormVo)) {
            ProcessFormVo processFormVo = new ProcessFormVo();
            // 表单内容
            String content = wfDeployFormVo.getContent();
            if (StrUtil.isNotBlank(content)) {
                Map<String, Object> formModel = JSON.parseObject(content, Map.class);
                if (MapUtil.isNotEmpty(formModel)) {
                    processFormVo.setFormModel(formModel);
                    processFormVo.setFormData(hisTaskIns.getTaskLocalVariables());
                    processFormVo.setFormBtns(false);
                }
            }
            return processFormVo;
        }
        return null;
    }

    // 获取流程状态的映射
    private Map<String, String> getProcessStatusMap(List<HistoricProcessInstance> instances) {
        Map<String, String> statusMap = new HashMap<>();
        for (HistoricProcessInstance instance : instances) {
            HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(instance.getId())
                    .variableName(ProcessConstants.PROCESS_STATUS_KEY)
                    .singleResult();
            if (ObjectUtil.isNotNull(variableInstance)) {
                statusMap.put(instance.getId(), Convert.toStr(variableInstance.getValue()));
            }
        }
        return statusMap;
    }

    // 获取流程分类的映射
    private Map<String, String> getCategoryMap(List<HistoricProcessInstance> instances) {
        Map<String, String> categoryMap = new HashMap<>();
        for (HistoricProcessInstance instance : instances) {
            Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(instance.getDeploymentId())
                    .singleResult();
            if (ObjectUtil.isNotNull(deployment)) {
                categoryMap.put(instance.getDeploymentId(), Convert.toStr(deployment.getCategory()));
            }
        }
        return categoryMap;
    }

}
