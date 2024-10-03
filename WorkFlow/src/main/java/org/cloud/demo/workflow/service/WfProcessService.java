package org.cloud.demo.workflow.service;


import org.cloud.demo.common.web.domain.TableDataInfo;
import org.cloud.demo.common.web.page.PageQuery;
import org.cloud.demo.workflow.domain.dto.ProcessQuery;
import org.cloud.demo.workflow.domain.vo.ProcessFormVo;
import org.cloud.demo.workflow.domain.vo.WfDefinitionVo;
import org.cloud.demo.workflow.domain.vo.WfMyProcessVo;
import org.cloud.demo.workflow.domain.vo.WfProcessDetailVo;

import java.util.Map;

public interface WfProcessService {
    /**
     * 查询流程部署关联表单信息
     *
     * @param definitionId 流程定义id
     * @param deployId     流程部署id
     * @param procInsId
     */
    ProcessFormVo selectFormContent(String definitionId, String deployId, String procInsId);

    /**
     * 根据流程定义id启动流程实例
     *
     * @param processDefId 流程定义id
     * @param variables    变量集合,json对象
     */
    void startProcessByDefId(String processDefId, Map<String, Object> variables);

    /**
     * 查询可发起流程列表
     *
     * @param processQuery 查询参数
     * @param pageQuery    分页参数
     */
    TableDataInfo<WfDefinitionVo> selectPageStartProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * 获取当前用户的发起的流程列表
     *
     * @param processQuery 查询条件
     * @param pageQuery    分页查询条件
     * @return 发起的流程列表
     */
    TableDataInfo<WfMyProcessVo> selectPageOwnProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * 查询流程详情信息
     *
     * @param procInsId 流程实例ID
     * @param taskId    任务ID
     */
    WfProcessDetailVo queryProcessDetail(String procInsId, String taskId);
}
