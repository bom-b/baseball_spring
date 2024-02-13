package com.baseball.config;

import com.baseball.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthService authService;
    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "https://bulls-and-cows.kr/", "https://www.bulls-and-cows.kr/", "https://www.bulls-and-cows.shop/", "https://bulls-and-cows.shop/"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));

        corsConfiguration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return urlBasedCorsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
                .formLogin((login) -> login.disable())
                .httpBasic((basic) -> basic.disable())
                // HTTP 요청에 대한 보안 필터 체인을 구성
                // .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
                .authorizeHttpRequests((auth) -> auth
                        // 토큰 검증을 하지 않을 요청
                        .requestMatchers("/hello","/auth/**","/kakao/**", "https://kauth.kakao.com/**", "https://kapi/kakao.com/**")
                        .permitAll()
                        // 그 외의 모든 요청은 토큰이 있어야 접근 가능
                        .anyRequest().authenticated())
                // JWT 토큰 필터 추가: JwtTokenFilter를 BasicAuthenticationFilter 전에 추가하여 JWT 토큰을 검증
                .addFilterBefore(new JWTFilter(authService, secretKey), UsernamePasswordAuthenticationFilter.class)
                // 세션 정책 설정: SessionCreationPolicy.STATELESS로 설정하여 세션 기반 인증을 사용하지 않도록 한다.
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 로그아웃 기능 비활성화: 상태를 유지하지 않는 인증 방식에서는 로그아웃이 필요 없다.
                .logout((logout) -> logout.disable());
        return http.build();
    }

    @Bean
    // 암호화 (인코더)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}