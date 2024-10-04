package org.cloud.demo.workflow.domain.vo;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部署实例和表单关联视图对象
 */
@Data
public class WfDeployFormVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6611039702831290592L;
    /**
     * 流程部署主键
     */
    private String deployId;

    /**
     * 表单Key
     */
    private String formKey;

    /**
     * 节点Key
     */
    private String nodeKey;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 表单内容
     */
    private String content;
}
