package com.yuhang.demo.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuhang.demo.common.entity.BaseEntity;
import lombok.Data;

@TableName("sys_user_role")
@Data
public class SysUserRole {

    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;
}
