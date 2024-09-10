package org.cloud.demo.workflow.service;


import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.ProcessQuery;
import org.cloud.demo.workflow.domain.vo.ProcessFormVo;
import org.cloud.demo.workflow.domain.vo.WfDefinitionVo;

import java.util.Map;

public interface WfProcessService {
    ProcessFormVo selectFormContent(String definitionId, String deployId, String procInsId);

    void startProcessByDefId(String processDefId, Map<String, Object> variables);
    /**
     * 查询可发起流程列表
     *
     * @param processQuery 查询参数
     * @param pageQuery 分页参数
     */
    TableDataInfo<WfDefinitionVo> selectPageStartProcessList(ProcessQuery processQuery, PageQuery pageQuery);
}
