package com.yuhang.demo.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhang.demo.system.entity.SysRole;
import com.yuhang.demo.system.mapper.SysRoleMapper;
import com.yuhang.demo.system.service.SysRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
