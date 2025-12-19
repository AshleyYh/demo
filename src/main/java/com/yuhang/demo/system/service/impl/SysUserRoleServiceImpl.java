package com.yuhang.demo.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhang.demo.system.entity.SysUserRole;
import com.yuhang.demo.system.mapper.SysUserRoleMapper;
import com.yuhang.demo.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
}
