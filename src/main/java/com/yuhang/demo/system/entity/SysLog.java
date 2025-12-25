package com.yuhang.demo.system.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuhang.demo.common.converter.LogStatusConverter;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(collection = "sys_operation_log")
@CompoundIndex(name = "idx_title_time", def = "{'title': 1, 'operTime': -1}")
public class SysLog {

    @Id
    private String id;

    @Indexed
    @ExcelProperty("模块标题")
    @ColumnWidth(20) // 设置列宽
    private String title;

    @ExcelProperty("操作类型")
    private String businessType;   // 业务类型
    private String method;         // 方法名称
    private String requestMethod;  // 请求方式 (GET/POST)

    @ExcelProperty("操作人员")
    private String operator;

    @ExcelProperty("主机地址")
    @ColumnWidth(15)
    private String operIp;         // 请求IP

    @ExcelProperty("操作地点")
    @ColumnWidth(25)
    private String operLocation;   // 地址
    private String operUrl;        // 请求URL
    private String queryParam;     // 请求参数
    private String jsonResult;     // 返回结果

    @ExcelProperty(value = "操作状态", converter = LogStatusConverter.class)
    private Integer status;        // 状态 (0正常 1异常)
    private String errorMsg;       // 错误消息

    @ExcelProperty("消耗时间(ms)")
    private Long costTime;         // 消耗时间(ms)

    @ExcelProperty("操作时间")
    @ColumnWidth(20)
    @Field("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Indexed(expireAfter = "30d") // 30天后自动删除
    private LocalDateTime operTime;// 操作时间
}
