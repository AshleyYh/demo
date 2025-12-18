package com.yuhang.demo.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhang.demo.auth.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {
}
