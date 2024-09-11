package org.cloud.demo.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.auth.domain.Menu;
import org.cloud.demo.auth.domain.dto.MenuDTO;
import org.cloud.demo.auth.mapper.MenuMapper;
import org.cloud.demo.auth.service.MenuService;
import org.cloud.demo.common.web.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {
    private final MenuMapper menuMapper;

    @Override
    @Transactional
    public int insertMenu(MenuDTO menuDTO) {
        // 检查是否存在
        checkMenuName(menuDTO.getMenuName());
        // 检查是否外链
        checkFrame(menuDTO.getIsFrame(), menuDTO.getPath());
        // 保存
        Menu menu = BeanUtil.copyProperties(menuDTO, Menu.class);
        return menuMapper.insert(menu);
    }

    private void checkFrame(String isFrame, String path) {
        if (StrUtil.equals(isFrame, "0")) {
            boolean url = Validator.isUrl(path);
            if (!url) {
                throw new ServiceException("外链格式不对，地址必须以http(s)://开头");
            }
        }
    }

    private void checkMenuName(String menuName) {
        boolean exists = menuMapper.exists(lambdaQuery(Menu.class).eq(Menu::getMenuName, menuName));
        if (exists) {
            throw new ServiceException("菜单名称已经存在");
        }
    }
}
