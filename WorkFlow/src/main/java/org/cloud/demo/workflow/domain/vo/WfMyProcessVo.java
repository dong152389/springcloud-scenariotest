package org.cloud.demo.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 发起的流程的VO
 */
@Data
public class WfMyProcessVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2782292267458096205L;
    /**
     * 流程ID
     */
    private String procDefId;
    /**
     * 流程key
     */
    private String procDefKey;
    /**
     * 流程定义名称
     */
    private String procDefName;
    /**
     * 流程定义内置使用版本
     */
    private int procDefVersion;
    /**
     * 流程实例ID
     */
    private String procInsId;
    /**
     * 类别
     */
    private String category;
    /**
     * 当前节点
     */
    private String currentNode;

    /**
     * 任务耗时
     */
    private String duration;

    /**
     * 流程状态
     */
    private String processStatus;
    /**
     * 流程提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;
    /**
     * 任务完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

}
