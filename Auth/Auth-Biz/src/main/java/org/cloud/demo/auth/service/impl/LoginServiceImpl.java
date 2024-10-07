package org.cloud.demo.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.component.LoginHelper;
import org.cloud.demo.auth.domain.User;
import org.cloud.demo.auth.domain.dto.LoginDTO;
import org.cloud.demo.auth.mapper.MenuMapper;
import org.cloud.demo.auth.mapper.RoleMapper;
import org.cloud.demo.auth.mapper.UserMapper;
import org.cloud.demo.auth.service.LoginService;
import org.cloud.demo.common.constants.CacheConstants;
import org.cloud.demo.common.constants.UserConstants;
import org.cloud.demo.common.domain.LoginUser;
import org.cloud.demo.common.domain.RoleDTO;
import org.cloud.demo.common.enums.DeviceType;
import org.cloud.demo.common.enums.UserStatus;
import org.cloud.demo.common.utils.StringUtils;
import org.cloud.demo.common.web.exception.ServiceException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;

    /**
     * 用户登录方法
     *
     * @param loginDto 登录信息
     * @return 登录成功后的token
     */
    @Override
    public String login(LoginDTO loginDto) {
        User user = loadUserByUsername(loginDto.getUsername());
        checkLogin(loginDto, user.getPassword());
        // 构建登录用户信息
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        LoginHelper.loginByDevice(loginUser, DeviceType.PC);
        return StpUtil.getTokenValue();
    }

    /**
     * 构建登录用户信息
     *
     * @param user 用户信息
     * @return 登录用户信息
     */
    private LoginUser buildLoginUser(User user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setLoginTime(new Date());
        loginUser.setIpaddr(NetUtil.getLocalhostStr());
        loginUser.setExpireTime(DateUtil.offsetDay(new Date(), UserConstants.TOKEN_EXPIRE));
        loginUser.setOs(SystemUtil.getOsInfo().getName());
        loginUser.setNickName(user.getNickName());
        List<RoleDTO> roleDTOS = roleMapper.selectRoleByUserId(user.getUserId());
        loginUser.setRoles(roleDTOS);
        loginUser.setMenuPermission(getMenuPermission(user));
        loginUser.setRolePermission(getRolePermission(roleDTOS));
        return loginUser;
    }


    /**
     * 获取用户角色权限集合
     *
     * @param roleDTOS 角色列表
     * @return 用户权限集合
     */
    private Set<String> getRolePermission(List<RoleDTO> roleDTOS) {
        Set<String> permsSet = new HashSet<>();
        for (RoleDTO perm : roleDTOS) {
            if (ObjectUtil.isNotNull(perm)) {
                permsSet.addAll(StringUtils.splitList(perm.getRoleKey().trim()));
            }
        }
        return permsSet;
    }

    /**
     * 获取用户的菜单权限
     *
     * @param user 用户对象
     * @return 返回用户的菜单权限集合
     */
    private Set<String> getMenuPermission(User user) {
        Set<String> perms = new HashSet<>();
        // 管理员拥有所有权限
        if (user.isAdmin()) {
            perms.add("*:*:*");
        } else {
            perms.addAll(menuMapper.selectMenuPermsByUserId(user.getUserId()));
        }
        return perms;
    }


    private User loadUserByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .select(User::getUserName, User::getStatus)
                .eq(User::getUserName, username));
        if (ObjectUtil.isNull(user)) {
            throw new ServiceException("登录用户：" + username + "不存在");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            throw new ServiceException("登录用户：" + username + "已被停用");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(User.class).eq(User::getUserName, username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 登录校验
     */
    private void checkLogin(LoginDTO loginDto, String encodePassword) {
        String password = loginDto.getPassword();
        String code = loginDto.getCode();
        String uuid = loginDto.getUuid();
        // 验证码校验
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
        String captcha = Convert.toStr(redisTemplate.opsForValue().get(verifyKey));
        redisTemplate.delete(verifyKey);
        if (captcha == null) {
            throw new ServiceException("验证码已失效");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new ServiceException("验证码错误");
        }
        // 密码校验
        if (!passwordEncoder.matches(password, encodePassword)) {
            throw new ServiceException("用户不存在/密码错误");
        }
    }
}
