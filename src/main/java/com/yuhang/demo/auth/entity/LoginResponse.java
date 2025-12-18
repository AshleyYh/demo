package com.yuhang.demo.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType; // 固定为 Bearer
    private Long expiresIn;   // 有效时长（毫秒或秒）
    private String username;  // 用户名
}
