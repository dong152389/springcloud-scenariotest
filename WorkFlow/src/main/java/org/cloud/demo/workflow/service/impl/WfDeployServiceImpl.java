package org.cloud.demo.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.exception.ServiceException;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.WfDeployForm;
import org.cloud.demo.workflow.domain.WfForm;
import org.cloud.demo.workflow.domain.dto.ProcessQuery;
import org.cloud.demo.workflow.domain.vo.WfDeployVo;
import org.cloud.demo.workflow.mapper.WfDeployFormMapper;
import org.cloud.demo.workflow.mapper.WfFormMapper;
import org.cloud.demo.workflow.service.WfDeployService;
import org.cloud.demo.workflow.utils.ModelUtils;
import org.cloud.demo.workflow.utils.ProcessUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WfDeployServiceImpl implements WfDeployService {
    private final WfDeployFormMapper wfDeployFormMapper;
    private final WfFormMapper wfFormMapper;
    private final RepositoryService repositoryService;


    /**
     * 保存内部部署表单信息
     *
     * @param deployId 部署ID
     * @param bpmnModel BPMN模型
     * @return 保存是否成功
     * @throws ServiceException 如果开始节点不存在，抛出此异常
     * @Transactional(rollbackFor = Exception.class) 如果在执行方法时发生异常，则回滚事务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveInternalDeployForm(String deployId, BpmnModel bpmnModel) {
        // 获取每个节点上的form表单ID进行保存
        List<WfDeployForm> deployFormList = new ArrayList<>();
        // 获取开始节点
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            throw new ServiceException("开始节点不存在，请检查流程设计是否有误！");
        }
        // 保存开始节点表单信息
        WfDeployForm startDeployForm = buildDeployForm(deployId, startEvent);
        if (ObjectUtil.isNotNull(startDeployForm)) {
            deployFormList.add(startDeployForm);
        }
        // 保存用户节点表单信息
        Collection<UserTask> userTasks = ModelUtils.getAllUserTaskEvent(bpmnModel);
        if (CollUtil.isNotEmpty(userTasks)) {
            for (UserTask userTask : userTasks) {
                WfDeployForm userTaskDeployForm = buildDeployForm(deployId, userTask);
                if (ObjectUtil.isNotNull(userTaskDeployForm)) {
                    deployFormList.add(userTaskDeployForm);
                }
            }
        }
        // 批量新增部署流程和表单关联信息
        return wfDeployFormMapper.insertBatch(deployFormList);
    }

    @Override
    public TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery) {
        // 比如这里查询出来用户的id和组
        String userId = "123L";
        String groupId = "321L";
        // 创建查询对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .startableByUserOrGroups(userId, Collections.singleton(groupId))
                .latestVersion()
                .orderByProcessDefinitionKey()
                .desc();
        // 封装查询条件
        ProcessUtils.buildProcessSearch(processDefinitionQuery, processQuery);
        long total = processDefinitionQuery.count();
        if (total <= 0) {
            return TableDataInfo.build();
        }
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        int pageSize = pageQuery.getPageSize();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(offset, pageSize);

        // 批量查询部署流程
        List<Deployment> deployments = repositoryService.createDeploymentQuery().deploymentIds(processDefinitionList.stream().map(ProcessDefinition::getDeploymentId).collect(Collectors.toList())).list();
        List<WfDeployVo> list = processDefinitionList.stream().map(processDefinition -> {
            // 查询部署
            WfDeployVo wfDeployVo = new WfDeployVo();
            wfDeployVo.setProcessDefId(processDefinition.getId());
            wfDeployVo.setProcessDefKey(processDefinition.getKey());
            wfDeployVo.setProcessDefName(processDefinition.getName());
            wfDeployVo.setVersion(processDefinition.getVersion());
            wfDeployVo.setCategory(processDefinition.getCategory());
            wfDeployVo.setDeployId(processDefinition.getDeploymentId());
            wfDeployVo.setSuspended(processDefinition.isSuspended());
            Deployment deployment = CollUtil.findOne(deployments, d -> d.getId().equals(processDefinition.getDeploymentId()));
            wfDeployVo.setDeployTime(deployment.getDeploymentTime());
            return wfDeployVo;
        }).collect(Collectors.toList());
        Page<WfDeployVo> page = new Page();
        page.setTotal(total);
        page.setRecords(list);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .orderByProcessDefinitionVersion()
                .desc();
        long total = processDefinitionQuery.count();
        if (total <= 0) {
            return TableDataInfo.build();
        }
        // 根据查询条件，查询所有版本
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        int pageSize = pageQuery.getPageSize();

        List<ProcessDefinition> list = processDefinitionQuery.listPage(offset, pageSize);
        //查询部署信息
        List<Deployment> deployments = repositoryService.createDeploymentQuery().deploymentIds(list.stream().map(ProcessDefinition::getDeploymentId).collect(Collectors.toList())).list();
        if (CollUtil.isEmpty(deployments)) {
            throw new ServiceException("部署信息有误!");
        }
        Map<String, Date> dateMap = deployments.stream().collect(Collectors.toMap(deployment -> deployment.getId(), deployment -> deployment.getDeploymentTime()));
        List<WfDeployVo> wfDeployVos = list.stream().map(item -> {
            WfDeployVo vo = new WfDeployVo();
            vo.setProcessDefId(item.getId());
            vo.setProcessDefKey(item.getKey());
            vo.setProcessDefName(item.getName());
            vo.setVersion(item.getVersion());
            vo.setCategory(item.getCategory());
            vo.setDeployId(item.getDeploymentId());
            vo.setSuspended(item.isSuspended());
            vo.setDeployTime(dateMap.get(item.getDeploymentId()));
            return vo;
        }).collect(Collectors.toList());
        Page<WfDeployVo> page = new Page<>();
        page.setRecords(wfDeployVos);
        page.setTotal(total);
        return TableDataInfo.build(page);
    }

    /**
     * 更新流程定义状态
     *
     * @param definitionId 流程定义ID
     * @param state 流程定义状态（ACTIVE激活或SUSPENDED挂起）
     * @throws IllegalArgumentException 如果state参数不为ACTIVE或SUSPENDED则抛出此异常
     */
    @Override
    @Transactional
    public void updateState(String definitionId, String state) {
        if (SuspensionState.ACTIVE.toString().equals(state)) {
            // 激活
            repositoryService.activateProcessDefinitionById(definitionId, true, null);
        } else if (SuspensionState.SUSPENDED.toString().equals(state)) {
            // 挂起
            repositoryService.suspendProcessDefinitionById(definitionId, true, null);
        }
    }

    @Override
    public String queryBpmnXmlById(String definitionId) {
        InputStream inputStream = repositoryService.getProcessModel(definitionId);
        return IoUtil.readUtf8(inputStream);
    }

    /**
     * 根据部署ID列表删除部署
     *
     * @param list 部署ID列表
     * @return 无返回值
     */
    @Override
    public void deleteByIds(List<String> list) {
        for (String deployId : list) {
            repositoryService.deleteDeployment(deployId, true);
        }
    }

    /**
     * 构建WfDeployForm
     *
     * @param deployId 部署ID
     * @param node     节点信息
     * @return {@link WfDeployForm}
     */
    private WfDeployForm buildDeployForm(String deployId, FlowNode node) {
        String formKey = ModelUtils.getFormKey(node);
        if (ObjectUtil.isNull(formKey)) {
            return null;
        }
        Long formId = Convert.toLong(StrUtil.removePrefixIgnoreCase(formKey, "key_"));
        WfForm wfForm = wfFormMapper.selectById(formId);
        if (ObjectUtil.isNull(wfForm)) {
            return null;
        }
        WfDeployForm deployForm = new WfDeployForm();
        deployForm.setDeployId(deployId);
        deployForm.setFormKey(formKey);
        deployForm.setFormName(wfForm.getName());
        deployForm.setNodeKey(node.getId());
        deployForm.setNodeName(node.getName());
        deployForm.setContent(wfForm.getContent());
        return deployForm;
    }

}
