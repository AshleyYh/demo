package com.yuhang.demo.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhang.demo.common.result.R;
import com.yuhang.demo.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    boolean saveUser(SysUser user);

    boolean deleteUser(Long id);
}
