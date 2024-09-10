package org.cloud.demo.workflow.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * MetaInfo 是 Flowable 中的 Model 对象的一个属性，通常用来存储模型的元数据信息。这些元数据通常以 JSON 格式存储，用来描述模型的额外信息:
 * 模型的描述信息
 * 版本号
 * 作者或创建者的名字
 * 创建时间或最后修改时间
 * 其他自定义的元数据信息
 */
@Data
public class ModelMetaInfoBO {
    private Integer version;
    private String desc;
    private String category;
    private String createUser;
    private String updateUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private Long formId;
}
