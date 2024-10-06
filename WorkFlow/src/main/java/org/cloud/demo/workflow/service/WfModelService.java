package org.cloud.demo.workflow.service;


import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.DeployModelDTO;
import org.cloud.demo.workflow.domain.dto.WfBpmnModelDTO;
import org.cloud.demo.workflow.domain.dto.WfModelDTO;
import org.cloud.demo.workflow.domain.vo.WfModelVo;

import java.util.List;

public interface WfModelService {
    void insert(WfModelDTO wfModelDTO);

    void deleteByIds(List<String> ids);

    void update(WfModelDTO wfModelDTO);

    TableDataInfo<WfModelVo> list(WfModelDTO wfModelDTO, PageQuery pageQuery);

    void saveBpmnXml(WfBpmnModelDTO wfBpmnModelDTO);

    void latest(String modelId);

    TableDataInfo<WfModelVo> historyList(WfModelDTO wfModelDTO, PageQuery pageQuery);

    WfModelVo getModel(String modelId);

    String queryBpmnXmlById(String modelId);

    boolean deploy(DeployModelDTO deployModelDTO);
}
