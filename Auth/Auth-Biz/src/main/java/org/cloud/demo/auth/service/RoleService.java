package org.cloud.demo.auth.service;

import jakarta.validation.Valid;
import org.cloud.demo.auth.domain.RoleMenu;

public interface RoleService {

    int insertRole(@Valid RoleDTO role);


    int bindMenu(@Valid RoleMenu roleMenu);
}
