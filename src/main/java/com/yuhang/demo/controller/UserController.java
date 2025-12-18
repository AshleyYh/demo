package com.yuhang.demo.controller;

import com.yuhang.demo.auth.entity.MyUserDetails;
import com.yuhang.demo.common.result.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/info")
    public R<String> getUserInfo() {
        // 如果能进来，说明 Token 解析成功且已授权
        return R.success("恭喜你，你已成功通过 JWT 验证！当前用户具有访问权限。");
    }

    /**
     * 只有拥有 ROLE_ADMIN 权力的用户才能访问
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public R<String> adminOnly() {
        return R.success("这是管理员专属接口，普通用户看不见我");
    }

    /**
     * 只有拥有 ROLE_USER 权力的用户才能访问
     */
    @GetMapping("/user-only")
    @PreAuthorize("hasRole('USER')")
    public R<String> userOnly() {
        return R.success("这是普通用户接口");
    }

    @GetMapping("/me")
    public R<MyUserDetails> getMyDetail(@AuthenticationPrincipal MyUserDetails userDetails) {
        // userDetails 就是当前发请求的那个人
        // 你可以从这里拿到 userId, username 等所有信息
        return R.success(userDetails);
    }
}
