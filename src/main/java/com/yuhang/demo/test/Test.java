package com.yuhang.demo.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        // 模拟你刚才生成的密文
        String encodedPassword = "$2a$10$zliQ.3qF.PR3F6BkIddeROST2E5kmosvfnPbtQKUj9NyY3KDljw1W";

        boolean isMatch = encoder.matches(rawPassword, encodedPassword);
        System.out.println("是否匹配: " + isMatch);
    }
}
