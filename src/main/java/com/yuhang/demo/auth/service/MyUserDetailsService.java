package com.yuhang.demo.auth.service;

import com.yuhang.demo.auth.entity.MyUserDetails;
import com.yuhang.demo.auth.entity.SysRole;
import com.yuhang.demo.auth.entity.SysUser;
import com.yuhang.demo.auth.mapper.RoleMapper;
import com.yuhang.demo.auth.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    public MyUserDetailsService(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Spring Security 正在加载用户: {}", username);

        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("用户名不能为空");
        }

        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            log.warn("用户不存在：{}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<SysRole> roles = userMapper.selectRoleByUserId(user.getId());

        UserDetails userDetails = new MyUserDetails(user, roles);

        log.info("用户加载成功: {}, 角色: {}", username,
                roles.stream().map(SysRole::getRoleName).toArray());

        return userDetails;
    }
}
