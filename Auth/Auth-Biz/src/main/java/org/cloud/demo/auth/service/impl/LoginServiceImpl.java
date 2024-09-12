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
import org.cloud.demo.auth.domain.bo.LoginUser;
import org.cloud.demo.auth.domain.dto.LoginDTO;
import org.cloud.demo.auth.mapper.UserMapper;
import org.cloud.demo.auth.service.LoginService;
import org.cloud.demo.common.constants.CacheConstants;
import org.cloud.demo.common.constants.UserConstants;
import org.cloud.demo.common.enums.DeviceType;
import org.cloud.demo.common.enums.UserStatus;
import org.cloud.demo.common.web.exception.ServiceException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

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

    private LoginUser buildLoginUser(User user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setLoginTime(new Date());
        loginUser.setIpaddr(NetUtil.getLocalhostStr());
        loginUser.setExpireTime(DateUtil.offsetDay(new Date(), UserConstants.TOKEN_EXPIRE));
        loginUser.setOs(SystemUtil.getOsInfo().getName());
        loginUser.setNickName(user.getNickName());
        return loginUser;
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
