package org.cloud.demo.workflow.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class WfProcessDetailVo {

    /**
     * 表单内容
     */
    private ProcessFormVo processFormVo;

    /**
     * 流程XML
     */
    private String bpmnXml;
    /**
     * 历史流程节点信息
     */
    private List<WfProcNodeVo> historyProcNodeList;

    /**
     * 流程执行过程
     */
    private WfViewerVo flowViewer;
}
