package com.yuhang.demo.system.service;

import com.yuhang.demo.common.domain.PageResult;
import com.yuhang.demo.system.domain.dto.LogQueryDTO;
import com.yuhang.demo.system.entity.SysLog;

import java.util.List;

public interface SysLogService {

    void saveLog(SysLog sysLog);

    PageResult<SysLog> selectLogList(LogQueryDTO queryDTO);

    List<SysLog> selectAllLogList(LogQueryDTO queryDTO);
}
