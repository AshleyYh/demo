package com.yuhang.demo.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhang.demo.system.entity.SysRole;
import com.yuhang.demo.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    List<SysRole> selectRoleByUserId(Long id);

    SysUser selectByUsername(String username);
}
