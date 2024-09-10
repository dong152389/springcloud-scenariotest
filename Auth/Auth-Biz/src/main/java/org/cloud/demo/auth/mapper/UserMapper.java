package org.cloud.demo.auth.mapper;

import org.cloud.demo.auth.domain.User;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.cloud.demo.common.db.BaseMapperPlus;

public interface UserMapper extends BaseMapperPlus<UserMapper, User, UserVo> {
}