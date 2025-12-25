package com.yuhang.demo.system.controller;

import com.yuhang.demo.common.annotation.Log;
import com.yuhang.demo.common.domain.PageResult;
import com.yuhang.demo.common.result.R;
import com.yuhang.demo.system.domain.dto.LogQueryDTO;
import com.yuhang.demo.system.entity.SysLog;
import com.yuhang.demo.system.service.SysLogService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/log")
public class SysLogController {

    private final SysLogService sysLogService;
    private final MongoTemplate mongoTemplate;

    public SysLogController(SysLogService sysLogService, MongoTemplate mongoTemplate) {
        this.sysLogService = sysLogService;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public R<PageResult<SysLog>> list(LogQueryDTO queryDTO) {
        return R.success(sysLogService.selectLogList(queryDTO));
    }

    @DeleteMapping("/clean")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(title = "日志管理", businessType = "DELETE")
    public R<String> clean() {
        mongoTemplate.dropCollection(SysLog.class);
        return R.success("日志清空成功");
    }
}
