package com.yuhang.demo.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyUserDetails implements UserDetails {

    // 原始用户信息
    private SysUser sysUser;

    // 用户的角色列表
    private List<SysRole> roles;

    // 用户的权限列表 (核心!)
    private Collection<? extends GrantedAuthority> authorities;

    // 构造方法中完成权限转换
    public MyUserDetails(SysUser sysUser, List<SysRole> roles) {
        this.sysUser = sysUser;
        this.roles = roles;

        // **将 SysRole 列表转换为 Spring Security 要求的 GrantedAuthority 列表**
        this.authorities = roles.stream()
                // 约定：角色编码以 "ROLE_" 开头
                .map(role -> new SimpleGrantedAuthority(role.getRoleCode()))
                .collect(Collectors.toList());
    }

    /**
     * 返回用户权限集合，这是鉴权的核心方法。
     * 权限通常是角色（ROLE_XXX）或具体的权限点（user:add）。
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /** 返回数据库中的加密密码 */
    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    /** 返回用户名 */
    @Override
    public String getUsername() {
        return sysUser.getUsername();
    }

    // 账户状态判断（根据 SysUser 实体中的 enabled 字段来判断）

    @Override
    public boolean isAccountNonExpired() {
        return true; // 默认不启用账户过期，实际项目需根据业务逻辑判断
    }

    @Override
    public boolean isAccountNonLocked() {
        return sysUser.getEnabled() != null && sysUser.getEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 默认密码不过期
    }

    @Override
    public boolean isEnabled() {
        // 账户是否可用
        return sysUser.getEnabled() != null && sysUser.getEnabled();
    }
}
