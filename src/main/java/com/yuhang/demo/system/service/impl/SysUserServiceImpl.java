package com.yuhang.demo.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhang.demo.common.constant.SecurityConstants;
import com.yuhang.demo.common.result.R;
import com.yuhang.demo.system.entity.MyUserDetails;
import com.yuhang.demo.system.entity.SysRole;
import com.yuhang.demo.system.entity.SysUser;
import com.yuhang.demo.system.entity.SysUserRole;
import com.yuhang.demo.system.mapper.SysUserMapper;
import com.yuhang.demo.system.service.SysRoleService;
import com.yuhang.demo.system.service.SysUserRoleService;
import com.yuhang.demo.system.service.SysUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final PasswordEncoder passwordEncoder;

    private final SysUserRoleService sysUserRoleService;

    private final SysRoleService sysRoleService;

    public SysUserServiceImpl(PasswordEncoder passwordEncoder, SysUserRoleService sysUserRoleService, SysRoleService sysRoleService) {
        this.passwordEncoder = passwordEncoder;
        this.sysUserRoleService = sysUserRoleService;
        this.sysRoleService = sysRoleService;
    }

    @Override
    @Transactional // 开启事务
    public boolean saveUser(SysUser user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("用户名已存在，请换一个吧");
        }

        String rawPassword = user.getPassword();
        if (!StringUtils.hasText(rawPassword)) {
            rawPassword = "123456"; // 默认密码
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);

        boolean  result = this.save(user);
        if (!result) {
            return false;
        }

        // 动态查询编码为 ‘ROLE_USER’ 的角色
        SysRole role = sysRoleService.getOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, SecurityConstants.ROLE_USER)
        );

        if (role == null) {
            throw new RuntimeException("默认用户角色不存在，请检查数据库配置");
        }
        // 保存 sys_user_role 表
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        // 4. 保存到数据库
        return sysUserRoleService.save(userRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        // 安全检查: 禁止删除初始管理员(id为1)
        if (id == SecurityConstants.ADMIN_USER_ID) {
            throw new RuntimeException("系统核心管理员禁止删除");
        }

        // 安全检查: 禁止删除当前登录用户
        // 用存在 SecurityContext 获取当前登录用户
        MyUserDetails currentUser = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getSysUser().getId().equals(id)) {
            throw new RuntimeException("禁止删除当前登录用户");
        }

        sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));

        return this.removeById(id);
    }

}
