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
import org.cloud.demo.common.domain.BaseEntity;

import java.io.Serializable;

/**
 * 菜单权限表
 */
@Schema(description="菜单权限表")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "sys_menu")
public class Menu extends BaseEntity implements Serializable {
    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.ASSIGN_ID)
    @Schema(description="菜单ID")
    @NotNull(message = "菜单ID不能为null")
    private Long menuId;

    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    @Schema(description="菜单名称")
    @Size(max = 50,message = "菜单名称最大长度要小于 50")
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    /**
     * 父菜单ID
     */
    @TableField(value = "parent_id")
    @Schema(description="父菜单ID")
    private Long parentId;

    /**
     * 显示顺序
     */
    @TableField(value = "order_num")
    @Schema(description="显示顺序")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @TableField(value = "`path`")
    @Schema(description="路由地址")
    @Size(max = 200,message = "路由地址最大长度要小于 200")
    private String path;

    /**
     * 组件路径
     */
    @TableField(value = "component")
    @Schema(description="组件路径")
    @Size(max = 255,message = "组件路径最大长度要小于 255")
    private String component;

    /**
     * 路由参数
     */
    @TableField(value = "query_param")
    @Schema(description="路由参数")
    @Size(max = 255,message = "路由参数最大长度要小于 255")
    private String queryParam;

    /**
     * 是否为外链（0是 1否）
     */
    @TableField(value = "is_frame")
    @Schema(description="是否为外链（0是 1否）")
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @TableField(value = "is_cache")
    @Schema(description="是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @TableField(value = "menu_type")
    @Schema(description="菜单类型（M目录 C菜单 F按钮）")
    @Size(max = 1,message = "菜单类型（M目录 C菜单 F按钮）最大长度要小于 1")
    private String menuType;

    /**
     * 显示状态（0显示 1隐藏）
     */
    @TableField(value = "visible")
    @Schema(description="显示状态（0显示 1隐藏）")
    @Size(max = 1,message = "显示状态（0显示 1隐藏）最大长度要小于 1")
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description="菜单状态（0正常 1停用）")
    @Size(max = 1,message = "菜单状态（0正常 1停用）最大长度要小于 1")
    private String status;

    /**
     * 权限标识
     */
    @TableField(value = "perms")
    @Schema(description="权限标识")
    @Size(max = 100,message = "权限标识最大长度要小于 100")
    private String perms;

    /**
     * 菜单图标
     */
    @TableField(value = "icon")
    @Schema(description="菜单图标")
    @Size(max = 100,message = "菜单图标最大长度要小于 100")
    private String icon;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description="备注")
    @Size(max = 500,message = "备注最大长度要小于 500")
    private String remark;
}