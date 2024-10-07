package org.cloud.demo.auth.mapper;

import org.cloud.demo.auth.domain.Role;
import org.cloud.demo.auth.domain.vo.RoleVo;
import org.cloud.demo.common.db.BaseMapperPlus;
import org.cloud.demo.common.domain.RoleDTO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RoleMapper extends BaseMapperPlus<RoleMapper, Role, RoleVo> {
    /**
     * 根据用户ID查询用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    List<RoleDTO> selectRoleByUserId(@Param("userId") Long userId);
}