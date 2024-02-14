package com.baseball.config;

import com.baseball.service.auth.AuthService;
import com.baseball.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final AuthService authService;

    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("로깅 - authorization: {}", authorization);

        // 로깅을 위해 현재 요청의 메서드와 경로를 출력
        log.info("로깅 - Request Method: {}", request.getMethod());
        log.info("로깅 - Request URI: {}", request.getRequestURI());

        // 토큰이 없으면 block
        if(authorization == null || !authorization.startsWith("Bearer ") || authorization.length() == "Bearer null".length() || authorization.length() == "Bearer undefined".length()) {
            log.warn("로깅 - authorization이 없거나, 잘못보냄.");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 꺼내기
        String token = authorization.split(" ")[1];

        // 토큰 expired 되었는지 확인. expired 되었다면 block
        if (JwtUtil.isExpired(token)) {
            log.error("로깅 - 토큰이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // userName token에서 가져오기
        Integer userName = JwtUtil.getID(token);

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

        // Detial 부여
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
