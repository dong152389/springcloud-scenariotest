package org.cloud.demo.workflow.mapper;

import org.apache.ibatis.annotations.Param;
import org.cloud.demo.common.db.BaseMapperPlus;
import org.cloud.demo.workflow.domain.ProcDefRole;

import java.util.List;

public interface ProcDefRoleMapper extends BaseMapperPlus<ProcDefRoleMapper,ProcDefRole,ProcDefRole> {
    /**
     * 根据角色ID列表查询流程定义ID列表
     *
     * @param roleIds 角色ID列表
     * @return 流程定义ID列表
     */
    List<String> selectProcDefIdByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 查询所有流程定义ID列表
     *
     * @return 返回一个包含所有流程定义ID的列表
     */
    List<String> selectProcDefIdList();
}