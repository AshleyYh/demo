package com.yuhang.demo.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhang.demo.auth.entity.SysRole;
import com.yuhang.demo.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
    List<SysRole> selectRoleByUserId(Long id);

    SysUser selectByUsername(String username);
}
