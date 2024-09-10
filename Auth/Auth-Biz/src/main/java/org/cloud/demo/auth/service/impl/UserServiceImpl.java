package org.cloud.demo.auth.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.User;
import org.cloud.demo.auth.domain.dto.UserRegisterDTO;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.cloud.demo.auth.mapper.UserMapper;
import org.cloud.demo.auth.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public int register(UserRegisterDTO registerDTO) {
        User user = new User();
        user.setUserName(registerDTO.getUserName());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickName(registerDTO.getNickName());
        user.setEmail(registerDTO.getEmail());
        user.setSex(registerDTO.getSex());
        user.setPhonenumber(registerDTO.getPhoneNumber());
        return userMapper.insert(user);
    }

    @Override
    public UserVo getUserInfo(Long userId) {
        return userMapper.selectVoOne(new LambdaQueryChainWrapper<>(userMapper).eq(User::getUserId, userId));
    }
}
