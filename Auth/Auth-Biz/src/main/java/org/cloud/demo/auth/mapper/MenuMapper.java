package org.cloud.demo.auth.mapper;

import org.apache.ibatis.annotations.Param;
import org.cloud.demo.auth.domain.Menu;
import org.cloud.demo.auth.domain.vo.MenuVo;
import org.cloud.demo.common.db.BaseMapperPlus;

import java.util.List;

public interface MenuMapper extends BaseMapperPlus<MenuMapper, Menu, MenuVo> {
    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(@Param("userId") Long userId);
}