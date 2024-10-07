package org.cloud.demo.auth.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cloud.demo.common.constants.UserConstants;
import org.cloud.demo.common.domain.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息表
 */
@Schema(description="用户信息表")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "sys_user")
public class User extends BaseEntity implements Serializable {
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    @Schema(description="用户ID")
    @NotNull(message = "用户ID不能为null")
    private Long userId;


    /**
     * 用户账号
     */
    @TableField(value = "user_name")
    @Schema(description="用户账号")
    @Size(max = 30,message = "用户账号最大长度要小于 30")
    @NotBlank(message = "用户账号不能为空")
    private String userName;

    /**
     * 用户昵称
     */
    @TableField(value = "nick_name")
    @Schema(description="用户昵称")
    @Size(max = 30,message = "用户昵称最大长度要小于 30")
    @NotBlank(message = "用户昵称不能为空")
    private String nickName;


    /**
     * 用户邮箱
     */
    @TableField(value = "email")
    @Schema(description="用户邮箱")
    @Size(max = 50,message = "用户邮箱最大长度要小于 50")
    private String email;

    /**
     * 手机号码
     */
    @TableField(value = "phonenumber")
    @Schema(description="手机号码")
    @Size(max = 11,message = "手机号码最大长度要小于 11")
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @TableField(value = "sex")
    @Schema(description="用户性别（0男 1女 2未知）")
    @Size(max = 1,message = "用户性别（0男 1女 2未知）最大长度要小于 1")
    private String sex;

    /**
     * 头像地址
     */
    @TableField(value = "avatar")
    @Schema(description="头像地址")
    @Size(max = 100,message = "头像地址最大长度要小于 100")
    private String avatar;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    @Schema(description="密码")
    @Size(max = 100,message = "密码最大长度要小于 100")
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description="帐号状态（0正常 1停用）")
    @Size(max = 1,message = "帐号状态（0正常 1停用）最大长度要小于 1")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableField(value = "del_flag")
    @Schema(description="删除标志（0代表存在 2代表删除）")
    @Size(max = 1,message = "删除标志（0代表存在 2代表删除）最大长度要小于 1")
    private String delFlag;

    /**
     * 最后登录IP
     */
    @TableField(value = "login_ip")
    @Schema(description="最后登录IP")
    @Size(max = 128,message = "最后登录IP最大长度要小于 128")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @TableField(value = "login_date")
    @Schema(description="最后登录时间")
    private Date loginDate;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description="备注")
    @Size(max = 500,message = "备注最大长度要小于 500")
    private String remark;

    public boolean isAdmin() {
        return UserConstants.ADMIN_ID.equals(this.userId);
    }
}