package org.cloud.demo.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.flowable.engine.task.Comment;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 流程节点信息
 */
@Data
public class WfProcNodeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4399877962391196488L;

    /**
     * 流程ID
     */
    private String procDefId;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 活动类型
     */
    private String activityType;
    /**
     * 活动耗时
     */
    private String duration;
    /**
     * 执行人Id
     */
    private Long assigneeId;
    /**
     * 执行人名称
     */
    private String assigneeName;
    /**
     * 候选执行人
     */
    private String candidate;
    /**
     * 任务意见
     */
    private List<Comment> commentList;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}