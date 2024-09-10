package org.cloud.demo.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.User;
import org.cloud.demo.auth.domain.dto.LoginDTO;
import org.cloud.demo.auth.mapper.UserMapper;
import org.cloud.demo.auth.service.LoginService;
import org.cloud.demo.common.web.exception.ServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean login(LoginDTO loginDto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, loginDto.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(user)) {
            throw new ServiceException("账号密码错误！");
        }
        //验证密码
        String password = user.getPassword();
        if (!passwordEncoder.matches(loginDto.getPassword(), password)) {
            throw new ServiceException("账号密码错误！");
        }
        StpUtil.login(user.getUserId());
        return true;
    }
}
