package com.yuhang.demo.system.controller;

import com.alibaba.excel.EasyExcel;
import com.yuhang.demo.common.annotation.Log;
import com.yuhang.demo.common.domain.PageResult;
import com.yuhang.demo.common.result.R;
import com.yuhang.demo.system.domain.dto.LogQueryDTO;
import com.yuhang.demo.system.entity.SysLog;
import com.yuhang.demo.system.service.SysLogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

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

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public void export(LogQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        // 1. 设置响应格式和编码
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        // 2. 防止中文文件名乱码
        String fileName = URLEncoder.encode("操作日志_" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 3. 获取数据（这里可以根据 queryDTO 查询所有符合条件的数据，不分页）
        // 注意：如果是百万级数据，建议分批次从 MongoDB 查询并写入
        List<SysLog> list = sysLogService.selectAllLogList(queryDTO);

        // 4. 使用 EasyExcel 写入流
        EasyExcel.write(response.getOutputStream(), SysLog.class)
                .sheet("操作日志")
                .doWrite(list);
    }
}
