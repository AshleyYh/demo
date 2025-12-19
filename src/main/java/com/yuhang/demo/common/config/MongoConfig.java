package com.yuhang.demo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoConfig {

    /**
     * 配置 MongoTemplate
     * 主要目的是移除保存时自动生成的 _class 字段
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory, MappingMongoConverter converter) {
        // 移除 _class 字段
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(factory, converter);
    }
}
