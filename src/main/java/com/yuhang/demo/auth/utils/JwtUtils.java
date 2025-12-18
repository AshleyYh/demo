package com.yuhang.demo.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    // 从配置文件 application.yml 中注入密钥（Base64 编码后的字符串）
    @Value("${jwt.secret}")
    private String secret;

    // 从配置文件注入 Token 有效期（单位：毫秒）
    @Value("${jwt.expiration}")
    private long expiration;

    // --- 核心方法：生成 Token ---

    /**
     * 根据 UserDetails 生成 JWT Token
     * @param userDetails 用户详情
     * @return JWT Token 字符串
     */
    public String createToken(UserDetails userDetails) {
        // 可以将用户的角色、ID等额外信息放入 Claims
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims) // 设置自定义 Claims
                .setSubject(userDetails.getUsername()) // 主题（通常是用户名）
                .setIssuedAt(new Date(System.currentTimeMillis())) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 签名算法和密钥
                .compact();
    }

    // --- 核心方法：验证 Token ---

    /**
     * 验证 Token 是否有效，包括是否过期以及是否与提供的 UserDetails 匹配
     * @param token JWT Token 字符串
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // 1. 用户名是否匹配 2. Token 是否未过期
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // --- 辅助方法：解析 Claims ---

    /**
     * 从 Token 中提取用户名 (Subject)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从 Token 中提取过期时间
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 提取 Token 中的某个 Claim
     * @param token JWT Token 字符串
     * @param claimsResolver 用于解析特定 Claim 的函数
     * @param <T> Claim 的类型
     * @return Claim 的值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析 JWT Token，获取所有 Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- 内部方法 ---

    /**
     * 判断 Token 是否过期
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 获取用于签名的 Key
     * 密钥必须至少 256 位，这里将 Base64 编码的密钥字符串转换为 Key 对象
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}