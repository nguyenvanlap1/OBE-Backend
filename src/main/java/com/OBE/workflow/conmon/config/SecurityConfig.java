package com.OBE.workflow.conmon.config;

import com.OBE.workflow.conmon.enums.SystemRoleType;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tách riêng ra để tránh lỗi cú pháp
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 2. Liệt kê đầy đủ các đường dẫn cần thiết của Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(SystemRoleType.ADMIN.name())
                        .requestMatchers("/camunda/**", "/lib/**", "/api/engine/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .bearerTokenResolver(request -> {
                            // KHÔNG giải mã token nếu đang vào đường dẫn login
                            if (request.getRequestURI().startsWith("/api/login")) {
                                return null;
                            }
                            // Các trường hợp khác thì lấy từ cookie như cũ
                            if (request.getCookies() != null) {
                                for (Cookie cookie : request.getCookies()) {
                                    if ("accessToken".equals(cookie.getName())) return cookie.getValue();
                                }
                            }
                            return null;
                        })
                        .jwt(jwt -> jwt
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        // Thêm filter và xử lý Exception bằng try-catch
        http.addFilterAfter((request, response, chain) -> {
            try {
                var auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    log.info(">>>> [SECURITY LOG] User: {} | Authorities: {}", auth.getName(), auth.getAuthorities());
                }
                chain.doFilter(request, response);
            } catch (Exception e) {
                log.error("Filter error: {}", e.getMessage());
                throw new RuntimeException(e); // Ép về RuntimeException để đúng chuẩn Lambda
            }
        }, BasicAuthenticationFilter.class);

        return http.build();
    }

    // THÊM ĐOẠN NÀY VÀO: Đây là bộ giải mã JWT bằng mã bí mật tự chế
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey((SecretKeySpec) jwtUtils.getSigningKey()).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        // JWT của bạn có: "roles": ["ADMIN"]
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(
                grantedAuthoritiesConverter
        );

        return authenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write(
                    "{\"status\": 401, \"message\": \"Token không hợp lệ hoặc đã hết hạn\"}"
            );
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}