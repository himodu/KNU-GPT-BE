package com.knugpt.knugpt.global.security.filter;

import com.knugpt.knugpt.domain.auth.repository.AuthRepository;
import com.knugpt.knugpt.global.constant.Constants;
import com.knugpt.knugpt.global.security.CustomUserDetails;
import com.knugpt.knugpt.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String authorizationHeader = request.getHeader("Authorization");
        log.info("Request URI: {}, Authorization Header: {}", uri, authorizationHeader);

        // jwt 헤더가 없는 경우 다음 필터로
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorizationHeader.substring(7); // 토큰 부분만 추출

        Claims claims = jwtTokenProvider.getClaimsFromToken(jwt);

        // refresh 토큰 검증으로 로그인 여부 검증
        Authentication authentication = jwtTokenProvider.getAuthenticationFromClaims(claims);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        authRepository.findRefreshTokenByAccountId(userDetails.getUserId());

        // Authentication 객체를 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);


        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Constants.NO_NEED_AUTH_URLS.contains(request.getRequestURI());
    }
}