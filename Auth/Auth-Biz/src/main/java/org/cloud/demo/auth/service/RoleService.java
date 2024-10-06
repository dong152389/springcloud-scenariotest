package org.cloud.demo.auth.service;

import jakarta.validation.Valid;
import org.cloud.demo.auth.domain.RoleMenu;
import org.cloud.demo.auth.domain.vo.RoleVo;
import org.cloud.demo.common.domain.RoleDTO;

import java.util.List;

public interface RoleService {

    int insertRole(@Valid RoleDTO role);


    int bindMenu(@Valid RoleMenu roleMenu);

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleIds 角色ID列表
     * @return 角色信息列表
     */
    List<RoleVo> queryRolesByRoleIds(List<Long> roleIds);

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    RoleVo queryRoleByRoleId(Long roleId);

    /**
     * 为指定角色分配用户
     *
     * @param roleId  角色ID
     * @param userIds 用户ID数组
     * @return 成功分配的用户数量
     */
    boolean selectAuthUser(Long roleId, Long[] userIds);
}
