package org.cloud.demo.workflow.mapper;

import org.apache.ibatis.annotations.Param;
import org.cloud.demo.common.db.BaseMapperPlus;
import org.cloud.demo.workflow.domain.ProcDefUser;

import java.util.List;

public interface ProcDefUserMapper extends BaseMapperPlus<ProcDefUserMapper, ProcDefUser, ProcDefUser> {
    /**
     * 根据用户ID查询流程定义ID列表
     *
     * @param userId 用户ID
     * @return 流程定义ID列表
     */
    List<String> selectProcDefIdByUserId(@Param("userId") Long userId);

    /**
     * 查询所有流程定义ID列表
     *
     * @return 流程定义ID列表
     */
    List<String> selectProcDefIdList();
}