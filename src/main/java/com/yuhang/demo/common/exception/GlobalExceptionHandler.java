package com.yuhang.demo.common.exception;

import com.yuhang.demo.common.result.R;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public R<String> handleBadCredentialsException(BadCredentialsException e) {
        return R.fail("用户名或密码不正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public R<String> handleAccessDeniedException(AccessDeniedException e) {
        return R.fail(403, "权限不足，拒绝访问");
    }
}
