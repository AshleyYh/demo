package com.yuhang.demo.system.domain.dto;

import lombok.Data;

@Data
public class LogQueryDTO {
    private Integer pageNum = 1;      // 当前页
    private Integer pageSize = 10;    // 每页条数
    private String title;             // 模块标题 (模糊搜索)
    private String operator;          // 操作人员 (模糊搜索)
    private Integer status;           // 状态 (0正常 1异常)
    private String beginTime;         // 开始时间 (yyyy-MM-dd HH:mm:ss)
    private String endTime;           // 结束时间 (yyyy-MM-dd HH:mm:ss)
}
