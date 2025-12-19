package com.yuhang.demo.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yuhang.demo.common.entity.BaseEntity;
import lombok.Data;

@TableName("sys_user")
@Data
public class SysUser extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("username")
    private String username;

    @TableField(select = false)
    private String password;

    @TableField("enabled")
    private Boolean enabled;

}
