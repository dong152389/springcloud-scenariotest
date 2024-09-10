package org.cloud.demo.workflow.service;

import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.ProcessQuery;
import org.cloud.demo.workflow.domain.vo.WfDeployVo;
import org.flowable.bpmn.model.BpmnModel;

import java.util.List;

public interface WfDeployService {
    boolean saveInternalDeployForm(String deployId, BpmnModel bpmnModel);

    TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery);

    TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery);

    void updateState(String definitionId, String state);

    String queryBpmnXmlById(String definitionId);

    void deleteByIds(List<String> list);

}
