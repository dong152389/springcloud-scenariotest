package org.cloud.demo.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.Role;
import org.cloud.demo.auth.domain.RoleMenu;
import org.cloud.demo.auth.domain.UserRole;
import org.cloud.demo.auth.domain.vo.RoleVo;
import org.cloud.demo.auth.mapper.RoleMapper;
import org.cloud.demo.auth.mapper.RoleMenuMapper;
import org.cloud.demo.auth.mapper.UserRoleMapper;
import org.cloud.demo.auth.service.RoleService;
import org.cloud.demo.common.domain.RoleDTO;
import org.cloud.demo.common.web.exception.ServiceException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    @Transactional
    public int insertRole(@Valid RoleDTO roleDTO) {
        // 检查是否重复
        checkRoleName(roleDTO.getRoleName());
        Role role = BeanUtil.copyProperties(roleDTO, Role.class);
        return roleMapper.insert(role);
    }

    @Override
    @Transactional
    public int bindMenu(RoleMenu roleMenu) {
        try {
            return roleMenuMapper.insert(roleMenu);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("当前角色已经绑定过了此菜单了");
        }
    }

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleIds 角色ID列表
     * @return 角色信息列表
     */
    @Override
    public List<RoleVo> queryRolesByRoleIds(List<Long> roleIds) {
        return roleMapper.selectVoList(new LambdaQueryWrapper<>(Role.class).in(Role::getRoleId, roleIds));
    }

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    @Override
    public RoleVo queryRoleByRoleId(Long roleId) {
        return roleMapper.selectVoById(roleId);
    }

    /**
     * 为指定角色分配用户
     *
     * @param roleId  角色ID
     * @param userIds 用户ID数组
     * @return 成功分配的用户数量
     */
    @Override
    @Transactional
    public boolean selectAuthUser(Long roleId, Long[] userIds) {
        List<UserRole> userRoles = new ArrayList<>();
        for (Long userId : userIds) {
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);
            userRoles.add(userRole);
        }
        return userRoleMapper.insertBatch(userRoles);
    }


    private void checkRoleName(String roleName) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", roleName);
        boolean exists = roleMapper.exists(queryWrapper);
        if (exists) {
            throw new ServiceException("角色名称已经存在了");
        }
    }
}
