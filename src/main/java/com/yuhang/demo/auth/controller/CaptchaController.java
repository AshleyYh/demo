package com.yuhang.demo.auth.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.yuhang.demo.common.result.R;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private final DefaultKaptcha defaultKaptcha;

    private final StringRedisTemplate redisTemplate;

    public CaptchaController(DefaultKaptcha defaultKaptcha, StringRedisTemplate redisTemplate) {
        this.defaultKaptcha = defaultKaptcha;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/captchaImage")
    public R<Map<String, Object>> getCode() {
        // 1. 生成验证码文本
        String code = defaultKaptcha.createText();
        // 2. 生成唯一标识 UUID
        String uuid = UUID.randomUUID().toString();
        String verifyKey = "captcha_codes:" + uuid;

        // 3. 存入 Redis，设置 2 分钟有效期
        redisTemplate.opsForValue().set(verifyKey, code, 2, TimeUnit.MINUTES);

        // 4. 生成图片流
        BufferedImage image = defaultKaptcha.createImage(code);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return R.fail("验证码生成失败");
        }

        Map<String, Object> ajax = new HashMap<>();
        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encodeBase64String(os.toByteArray()));
        return R.success(ajax);
    }
}
