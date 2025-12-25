package com.yuhang.demo.common.aspect;

import com.yuhang.demo.common.annotation.Log;
import com.yuhang.demo.common.constant.SecurityConstants;
import com.yuhang.demo.common.utils.AddressUtils;
import com.yuhang.demo.common.utils.IpUtils;
import com.yuhang.demo.system.entity.SysLog;
import com.yuhang.demo.system.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@Slf4j
public class LogAspect {

    private final SysLogService sysLogService;

    public LogAspect(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    // 1. 定义切入点：所有标注了 @Log 注解的方法
    @Pointcut("@annotation(com.yuhang.demo.common.annotation.Log)")
    public void logPointCut() {}

    // 2. 环绕通知
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        SysLog sysLog = new SysLog();

        try {
            // 执行目标方法
            result = point.proceed();
            sysLog.setStatus(SecurityConstants.SYS_LOG_SUCCESS);
        } catch (Throwable e) {
            sysLog.setStatus(SecurityConstants.SYS_LOG_FAILURE);
            sysLog.setErrorMsg(e.getMessage());
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            // 异步封装并保存日志
            saveLog(point, sysLog, result, costTime);
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, SysLog sysLog, Object result, long costTime) {
        // 获取 Request 属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 从注解中获取模块名和类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log annotation = signature.getMethod().getAnnotation(Log.class);

        sysLog.setTitle(annotation.title());
        sysLog.setBusinessType(annotation.businessType());
        sysLog.setCostTime(costTime);
        sysLog.setOperTime(LocalDateTime.now());
        sysLog.setOperUrl(request.getRequestURI());
        sysLog.setRequestMethod(request.getMethod());
        // sysLog.setOperIp(request.getRemoteAddr());
        // 在 LogAspect 的 saveLog 方法中
        String ip = IpUtils.getIpAddr(request);
        sysLog.setOperIp(ip);
        // 使用 ip2region 解析地点
        String address = AddressUtils.getRealAddressByIP(ip);
        // address 的结果可能是 "中国|0|上海|上海市|联通"
        // 你可以根据需要对字符串进行简单的处理，比如只保留 "上海市"
        sysLog.setOperLocation(address);
        sysLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        // 获取当前用户（从 SecurityContext 获取）
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            sysLog.setOperator(auth.getName());
        }
        // 保存到 MongoDB (生产环境建议这里调用一个带有 @Async 的 Service)
        CompletableFuture.runAsync(() -> {
            sysLogService.saveLog(sysLog);
        });
    }
}
