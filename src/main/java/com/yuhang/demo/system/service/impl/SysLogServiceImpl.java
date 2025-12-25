package com.yuhang.demo.system.service.impl;

import com.yuhang.demo.common.domain.PageResult;
import com.yuhang.demo.system.domain.dto.LogQueryDTO;
import com.yuhang.demo.system.entity.SysLog;
import com.yuhang.demo.system.service.SysLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class SysLogServiceImpl implements SysLogService {

    private final MongoTemplate mongoTemplate;

    public SysLogServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Async
    public void saveLog(SysLog sysLog) {
        mongoTemplate.save(sysLog);
    }

    @Override
    public PageResult<SysLog> selectLogList(LogQueryDTO queryDTO) {

        // 构建查询条件
        Query mongoQuery = new Query();

        // 模糊查询模块标题
        if (StringUtils.hasText(queryDTO.getTitle())) {
            // "i" 表示不区分大小写
            Pattern pattern = Pattern.compile("^.*" + queryDTO.getTitle() + ".*$", Pattern.CASE_INSENSITIVE);
            mongoQuery.addCriteria(Criteria.where("title").is(pattern));
        }

        // 精准匹配状态
        if (queryDTO.getStatus() != null) {
            mongoQuery.addCriteria(Criteria.where("status").is(queryDTO.getStatus()));
        }

        // 范围查询时间段
        if (StringUtils.hasText(queryDTO.getBeginTime()) && StringUtils.hasText(queryDTO.getEndTime())) {
            mongoQuery.addCriteria(Criteria.where("operTime")
                    .gte(queryDTO.getBeginTime())
                    .lte(queryDTO.getEndTime()));
        }

        // 查询总记录数
        long total = mongoTemplate.count(mongoQuery, SysLog.class);

        // 分页与排序
        Pageable pageable = PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(),
                Sort.by(Sort.Direction.DESC, "operTime"));
        mongoQuery.with(pageable);

        List<SysLog> list = mongoTemplate.find(mongoQuery, SysLog.class);

        return new PageResult<>(total, list);
    }
}
