package com.yuhang.demo.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuhang.demo.common.entity.BaseEntity;
import lombok.Data;

@TableName("sys_role")
@Data
public class SysRole {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("role_code")
    private String roleCode;

    @TableField("role_name")
    private String roleName;
}
