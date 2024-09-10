package org.cloud.demo.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cloud.demo.common.constants.ProcessConstants;
import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.exception.ServiceException;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.bo.ModelMetaInfoBO;
import org.cloud.demo.workflow.domain.dto.WfBpmnModelDTO;
import org.cloud.demo.workflow.domain.dto.WfModelDTO;
import org.cloud.demo.workflow.domain.vo.WfModelVo;
import org.cloud.demo.workflow.service.WfDeployService;
import org.cloud.demo.workflow.service.WfModelService;
import org.cloud.demo.workflow.utils.ModelUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WfModelServiceImpl implements WfModelService {

    private final RepositoryService repositoryService;
    private final WfDeployService wfDeployService;

    /**
     * 重构MetaInfo
     *
     * @param modelMetaInfoBO 模型元数据对象
     * @param latestVersion   最新版本
     * @return {@link ModelMetaInfoBO}
     */
    private static String rebuildMetaInfo(ModelMetaInfoBO modelMetaInfoBO, Integer latestVersion) {
        if (ObjectUtil.isNull(modelMetaInfoBO)) {
            return "{}";
        }
        modelMetaInfoBO.setVersion(latestVersion);
        modelMetaInfoBO.setUpdateTime(new Date());
        modelMetaInfoBO.setUpdateUser("admin");
        return JSON.toJSONString(modelMetaInfoBO);
    }

    @Transactional
    @Override
    public void insert(WfModelDTO wfModelDTO) {
        //创建一个模型
        Model model = repositoryService.newModel();
        model.setCategory(wfModelDTO.getCategory());
        model.setKey(wfModelDTO.getKey());
        model.setName(wfModelDTO.getName());
        model.setVersion(1);
        ModelMetaInfoBO modelMetaInfoBO = new ModelMetaInfoBO();
        modelMetaInfoBO.setCreateUser("admin");
        modelMetaInfoBO.setCreateTime(new Date());
        modelMetaInfoBO.setVersion(1);
        modelMetaInfoBO.setDesc(wfModelDTO.getDesc());
        model.setMetaInfo(JSON.toJSONString(modelMetaInfoBO));
        repositoryService.saveModel(model);
    }

    @Transactional
    @Override
    public void deleteByIds(List<String> ids) {
        for (String id : ids) {
            Model model = repositoryService.getModel(id);
            if (ObjectUtil.isNull(model)) {
                throw new ServiceException("流程模型不存在！");
            }
            repositoryService.deleteModel(id);
        }
    }

    @Override
    @Transactional
    public void update(WfModelDTO wfModelDTO) {
        String modelId = wfModelDTO.getId();
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new ServiceException("流程模型不存在！");
        }
        BeanUtil.copyProperties(wfModelDTO, model, CopyOptions.create(Model.class, true, "key"));
        // 这里并不设计流程编辑图编辑，版本不增加
        String metaInfo = model.getMetaInfo();
        ModelMetaInfoBO modelMetaInfoBO = JSON.parseObject(metaInfo, ModelMetaInfoBO.class);
        if (StrUtil.isNotBlank(wfModelDTO.getDesc())) {
            modelMetaInfoBO.setDesc(wfModelDTO.getDesc());
        }
        if (StrUtil.isNotBlank(wfModelDTO.getCategory())) {
            modelMetaInfoBO.setCategory(wfModelDTO.getCategory());
        }
        modelMetaInfoBO.setUpdateUser("admin");
        modelMetaInfoBO.setUpdateTime(new Date());
        model.setMetaInfo(JSON.toJSONString(modelMetaInfoBO));
        repositoryService.saveModel(model);
    }

    @Override
    public TableDataInfo<WfModelVo> list(WfModelDTO wfModelDTO, PageQuery pageQuery) {
        // 构建一个查询对象，查询最新的版本，历史版本在单独的历史分支查看
        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion();
        modelQuery.orderByCreateTime().desc();
        // 构建查询条件
        if (StrUtil.isNotBlank(wfModelDTO.getKey())) {
            modelQuery.modelKey(wfModelDTO.getKey());
        }
        if (StrUtil.isNotBlank(wfModelDTO.getName())) {
            modelQuery.modelNameLike("%" + wfModelDTO.getName() + "%");
        }
        if (StrUtil.isNotBlank(wfModelDTO.getCategory())) {
            modelQuery.modelCategoryLike("%" + wfModelDTO.getCategory() + "%");
        }
        // 查询总数
        long count = modelQuery.count();
        if (count <= 0) {
            return TableDataInfo.build();
        }
        //偏移量
        int offset = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();

        // 分页查询
        List<Model> models = modelQuery.listPage(offset, pageQuery.getPageSize());
        List<WfModelVo> modelVos = buildModelVo(models, false);
        //分页
        Page<WfModelVo> page = new Page<>();
        page.setTotal(count);
        page.setRecords(modelVos);
        return TableDataInfo.build(page);
    }

    /**
     * 这里要这样理解，设计流程图不能是在原来的基础上修改，而是新增，对版本号+1，然后保存
     *
     * @param wfModelDTO
     */
    @Override
    @Transactional
    public void saveBpmnXml(WfBpmnModelDTO wfModelDTO) {
        //查询模型信息
        String modelId = wfModelDTO.getModeId();
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new ServiceException("流程模型不存在！");
        }
        if (StrUtil.isBlank(wfModelDTO.getBpmnXml())) {
            throw new RuntimeException("获取模型设计图失败！");
        }
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(wfModelDTO.getBpmnXml());
        if (ObjectUtil.isNull(bpmnModel)) {
            throw new RuntimeException("请检查流程设计图！");
        }
        //获取开始节点
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            throw new RuntimeException("开始节点不存在，请检查流程设计是否有误！");
        }
        // 获取表单Key
        String formKey = startEvent.getFormKey();
        if (StrUtil.isBlank(formKey)) {
            throw new RuntimeException("请配置流程表单");
        }

        Model newModel = model;
        // 如果是第一次设计图，流程版本不新增
        if (Boolean.TRUE.equals(wfModelDTO.getNewVersion())) {
            // 如果当前版本不是1，创建新版本
            newModel = repositoryService.newModel();
            newModel.setName(model.getName());
            newModel.setKey(model.getKey());
            newModel.setCategory(model.getCategory());
            Integer latestVersion = model.getVersion() + 1;
            ModelMetaInfoBO modelMetaInfoBO = JSON.parseObject(model.getMetaInfo(), ModelMetaInfoBO.class);
            long formId = Long.parseLong(StrUtil.removePrefix(formKey, "key_"));
            modelMetaInfoBO.setFormId(formId);
            newModel.setMetaInfo(rebuildMetaInfo(modelMetaInfoBO, latestVersion));
            newModel.setVersion(latestVersion);
        }
        repositoryService.saveModel(newModel);
        // 保存流程图
        byte[] xmlByte = StrUtil.bytes(wfModelDTO.getBpmnXml(), CharsetUtil.UTF_8);
        repositoryService.addModelEditorSource(newModel.getId(), xmlByte);
    }

    @Override
    @Transactional
    public void latest(String modelId) {
        // 获取模型
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new ServiceException("流程模型不存在！");
        }
        // 获取最新的模型版本号,模型的key是不变的
        String modelKey = model.getKey();
        Model latestModel = repositoryService.createModelQuery().modelKey(modelKey).latestVersion().singleResult();
        // 判断当前模型是不是最新的
        if (model.getVersion().equals(latestModel.getVersion())) {
            throw new ServiceException("当前模型就是最新版本！");
        }
        // 最新版本号
        Integer latestVersion = latestModel.getVersion() + 1;
        // 将id设置为空，保存为新模型
        Model newModel = repositoryService.newModel();
        newModel.setName(model.getName());
        newModel.setKey(model.getKey());
        newModel.setCategory(model.getCategory());
        newModel.setVersion(latestVersion);
        newModel.setMetaInfo(model.getName());
        String metaInfo = model.getMetaInfo();
        ModelMetaInfoBO modelMetaInfoBO = JSON.parseObject(metaInfo, ModelMetaInfoBO.class);
        newModel.setMetaInfo(rebuildMetaInfo(modelMetaInfoBO, latestVersion));
        // 保存流程模型
        repositoryService.saveModel(newModel);
    }

    @Override
    public TableDataInfo<WfModelVo> historyList(WfModelDTO wfModelDTO, PageQuery pageQuery) {
        // 根据版本号倒序
        ModelQuery modelQuery = repositoryService.createModelQuery()
                .modelKey(wfModelDTO.getKey())
                .orderByModelVersion()
                .desc();
        //TODO 目前还做不了在历史模型中进行条件检索，因为如果在modelQuery中增加检索条件，那么排除的第一条可能不是最新一条

        // 去掉最新版
        long pageTotal = modelQuery.count() - 1;
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        int offset = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        // 如果是第一页，跳过第一条记录
        if (pageQuery.getPageNum() == 1) {
            offset += 1;
        }
        int pageSize = pageQuery.getPageSize();
        List<Model> models = modelQuery.listPage(offset, pageSize);
        List<WfModelVo> list = buildModelVo(models, false);

        Page<WfModelVo> page = new Page<>();
        page.setTotal(pageTotal);
        page.setRecords(list);
        return TableDataInfo.build(page);
    }

    @Override
    public WfModelVo getModel(String modelId) {
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new ServiceException("当前模型不存在！");
        }
        return buildModelVo(Collections.singletonList(model), true).get(0);
    }

    @Override
    public String queryBpmnXmlById(String modelId) {
        byte[] bytes = repositoryService.getModelEditorSource(modelId);
        return StrUtil.utf8Str(bytes);
    }

    @Override
    public boolean deploy(String modelId) {
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new ServiceException("流程模型不存在！");
        }
        // 以字节数组返回流程设计图
        byte[] bpmXmlByte = repositoryService.getModelEditorSource(modelId);
        if (ArrayUtil.isEmpty(bpmXmlByte)) {
            throw new ServiceException("请先设计流程图！");
        }
        String xml = StrUtil.utf8Str(bpmXmlByte);
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(xml);
        // 流程名称
        String processName = model.getName() + ProcessConstants.SUFFIX;
        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .category(model.getCategory())
                .name(model.getName())
                .key(model.getKey())
                .addBpmnModel(processName, bpmnModel)
                .deploy();
        // 创建一个查询流程对象，根据部署ID查询
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        // 修改流程定义的分类，以便于搜索
        repositoryService.setProcessDefinitionCategory(processDefinition.getId(), model.getCategory());
        // 设置流程定义的权限，建议抽取成一个方法，如果是集合遍历添加
        // 用户
        repositoryService.addCandidateStarterUser(processDefinition.getId(), "123L");
        // 组
        repositoryService.addCandidateStarterGroup(processDefinition.getId(), "321L");
        // 保存部署表单
        return wfDeployService.saveInternalDeployForm(deployment.getId(), bpmnModel);
    }

    /**
     * 构建model对象
     *
     * @param models  model集合
     * @param bpmnXml 是否需要封装bpmnXml
     * @return 模型集合
     */
    private List<WfModelVo> buildModelVo(List<Model> models, boolean bpmnXml) {
        return models.stream().map(model -> {
            WfModelVo wfModelVo = new WfModelVo();
            wfModelVo.setModelId(model.getId());
            wfModelVo.setModelName(model.getName());
            wfModelVo.setModelKey(model.getKey());
            wfModelVo.setCategory(model.getCategory());
            wfModelVo.setVersion(model.getVersion());
            String metaInfo = model.getMetaInfo();
            if (StrUtil.isNotBlank(metaInfo)) {
                ModelMetaInfoBO modelMetaInfoBO = JSON.parseObject(metaInfo, ModelMetaInfoBO.class);
                wfModelVo.setDescription(modelMetaInfoBO.getDesc());
                wfModelVo.setFormId(modelMetaInfoBO.getFormId());
                wfModelVo.setCreateTime(modelMetaInfoBO.getCreateTime());
                wfModelVo.setUpdateTime(modelMetaInfoBO.getUpdateTime());
                wfModelVo.setCreateUser(modelMetaInfoBO.getCreateUser());
                wfModelVo.setUpdateUser(modelMetaInfoBO.getUpdateUser());
            }
            if (bpmnXml) {
                byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
                String bpmn = StrUtil.utf8Str(modelEditorSource);
                wfModelVo.setBpmnXml(bpmn);
            }
            return wfModelVo;
        }).collect(Collectors.toList());
    }

}
