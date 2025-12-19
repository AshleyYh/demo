package com.yuhang.demo.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhang.demo.system.entity.MyUserDetails;
import com.yuhang.demo.system.entity.SysUser;
import com.yuhang.demo.common.result.R;
import com.yuhang.demo.system.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final SysUserService userService;

    public UserController(SysUserService userService) {
        this.userService = userService;
    }

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

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')") // 只有管理员才能访问
    public R<IPage<SysUser>> list(@RequestParam(defaultValue = "1") Integer current,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) String username) {
        Page<SysUser> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 如果传了用户名，则按用户名模糊查询
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username);
        // 按创建时间倒序查询
        // wrapper.orderByDesc(SysUser::getCreateTime);

        // 执行分页查询
        Page<SysUser> result = userService.page(page, wrapper);

        // 清空密码
        result.getRecords().forEach(user -> user.setPassword(null));

        return R.success(result);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')") // 只有管理员才能添加用户
    public R<String> addUser(@RequestBody SysUser user) {
        boolean success = userService.saveUser(user);
        if (success) {
            return R.success("用户添加成功");
        } else {
            return R.fail("用户添加失败");
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<String> deleteUser(@PathVariable Long id) {
        try {
            boolean success = userService.deleteUser(id);
            return success ? R.success("用户删除成功") : R.fail("用户不存在或删除失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
