package com.yuhang.demo.auth.controller;

import com.yuhang.demo.auth.entity.LoginRequest;
import com.yuhang.demo.auth.entity.LoginResponse;
import com.yuhang.demo.auth.entity.MyUserDetails;
import com.yuhang.demo.auth.utils.JwtUtils;
import com.yuhang.demo.common.result.R;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户登录接口，认证成功后返回 JWT Token
     */
    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        // 1. 将用户名和密码封装成 Spring Security 要求的 Token 对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        Authentication authentication;
        try {
            // 2. 调用 AuthenticationManager 进行认证
            // 这一步会委托给 UserDetailsService 去查询用户，并用 PasswordEncoder 校验密码
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            String msg;
            if (e instanceof BadCredentialsException) {
                msg = "用户名或密码错误";
            } else if (e instanceof DisabledException) {
                msg = "账户已被禁用，请联系管理员";
            } else if (e instanceof LockedException) {
                msg = "账户已被锁定";
            } else if (e instanceof InternalAuthenticationServiceException) {
                msg = "内部认证服务异常，请检查数据库连接";
            } else {
                msg = "登录失败，请稍后重试";
            }
            return R.fail(msg);
        }

        // 3. 认证成功，获取认证主体 (UserDetails)
        // 认证成功后，authentication.getPrincipal() 存储的是我们自定义的 MyUserDetails 对象
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        // 4. 生成 JWT Token
        String token = jwtUtils.createToken(userDetails);

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                3600L * 1000,
                userDetails.getUsername()
        );

        // 5. 返回统一成功响应，将 Token 放入 data 字段
        return R.success(response);
    }
}
