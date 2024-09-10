package org.cloud.demo.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户注册对象")
public class UserRegisterDTO {
    @Size(max = 30, message = "用户账号最大长度要小于 30")
    @NotBlank(message = "用户账号不能为空")
    private String userName;
    @Schema(description="用户昵称")
    @Size(max = 30,message = "用户昵称最大长度要小于 30")
    @NotBlank(message = "用户昵称不能为空")
    private String nickName;
    @Schema(description = "密码")
    @Size(max = 100, message = "密码最大长度要小于 100")
    private String password;
    @Schema(description = "用户昵称")
    @Size(max = 30, message = "用户昵称最大长度要小于 30")
    @NotBlank(message = "用户昵称不能为空")
    private String email;
    @Schema(description = "手机号码")
    @Size(max = 11, message = "手机号码最大长度要小于 11")
    private String phoneNumber;
    @Schema(description = "用户性别（0男 1女 2未知）")
    @Size(max = 1, message = "用户性别（0男 1女 2未知）最大长度要小于 1")
    private String sex;
    @Schema(description = "头像地址")
    @Size(max = 100, message = "头像地址最大长度要小于 100")
    private String avatar;

}
