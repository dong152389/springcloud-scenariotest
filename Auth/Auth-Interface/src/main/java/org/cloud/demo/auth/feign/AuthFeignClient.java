package org.cloud.demo.auth.feign;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.cloud.demo.auth.domain.vo.RoleVo;
import org.cloud.demo.auth.domain.vo.UserVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AuthFeignClient {

    @GetMapping("user/info/{userId}")
    UserVo getUserInfo(@NotNull(message = "用户ID不能为空") @PathVariable Long userId);

    @GetMapping("role/queryRolesByRoleIds")
    List<RoleVo> queryRolesByRoleIds(@NotEmpty(message = "角色ID不能为空") @RequestParam List<Long> roleIds);

    @GetMapping("role/queryRoleByRoleId")
    RoleVo queryRoleByRoleId(@NotNull(message = "角色ID不能为空") @RequestParam Long roleId);
}
