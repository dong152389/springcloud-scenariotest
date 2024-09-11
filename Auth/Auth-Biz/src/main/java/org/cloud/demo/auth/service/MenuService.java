package org.cloud.demo.auth.service;

import org.cloud.demo.auth.domain.dto.MenuDTO;

public interface MenuService {
    int insertMenu(MenuDTO menuDTO);
}
