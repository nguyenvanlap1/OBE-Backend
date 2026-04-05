package com.OBE.workflow.conmon.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${obe.jwt.secret}")
    private String secretKey;

    @Value("${obe.jwt.expiration}")
    private long expiration;
    /**
     * Hàm static lấy thời gian sống tối đa (tính bằng mili giây)
     */
    public long getMaxExpiration() {
        return this.expiration;
    }

    public Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String username, List<Map<String, String>> userContexts) {
        return Jwts.builder()
                .setSubject(username)
                // Thay vì claim "roles", ta dùng claim "ctx" (viết tắt của contexts cho nhẹ Token)
                .claim("ctx", userContexts)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}