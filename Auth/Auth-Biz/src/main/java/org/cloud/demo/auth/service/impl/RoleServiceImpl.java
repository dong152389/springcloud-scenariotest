package org.cloud.demo.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.Role;
import org.cloud.demo.auth.domain.RoleMenu;
import org.cloud.demo.auth.mapper.RoleMapper;
import org.cloud.demo.auth.mapper.RoleMenuMapper;
import org.cloud.demo.auth.service.RoleService;
import org.cloud.demo.common.web.exception.ServiceException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;

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


    private void checkRoleName(String roleName) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", roleName);
        boolean exists = roleMapper.exists(queryWrapper);
        if (exists) {
            throw new ServiceException("角色名称已经存在了");
        }
    }
}
