package com.yuhang.demo.common.utils;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;

public class AddressUtils {

    private static Searcher searcher;

    // 项目启动时加载数据文件到内存中
    static {
        try {
            // 获取资源文件路径
            ClassPathResource resource = new ClassPathResource("data/ip2region_v6.xdb");
            InputStream inputStream = resource.getInputStream();
            // 将文件内容读取为字节数组（空间换时间，查询飞快）
            byte[] cBuff = FileCopyUtils.copyToByteArray(inputStream);
            // 初始化 Searcher
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            System.err.println("failed to create content cached searcher: " + e.getMessage());
        }
    }

    /**
     * 根据IP获取地名
     * 格式示例：中国|0|广东省|深圳市|电信
     */
    public static String getRealAddressByIP(String ip) {
        if (ip == null || ip.equals("127.0.0.1") || ip.startsWith("192.168")) {
            return "内网IP";
        }
        try {
            return searcher.search(ip);
        } catch (Exception e) {
            return "未知地址";
        }
    }
}
