package org.cloud.demo.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.User;
import org.cloud.demo.auth.domain.dto.LoginDTO;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.cloud.demo.auth.mapper.UserMapper;
import org.cloud.demo.auth.service.LoginService;
import org.cloud.demo.common.web.exception.ServiceException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;

    @Override
    public SaTokenInfo login(LoginDTO loginDto) {
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

        // 生成token
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
        redisTemplate.opsForValue().set(tokenInfo.getTokenValue(), userVo);
        return tokenInfo;
    }
}
