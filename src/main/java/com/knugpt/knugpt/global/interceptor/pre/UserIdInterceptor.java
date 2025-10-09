package com.knugpt.knugpt.global.interceptor.pre;

import com.knugpt.knugpt.global.constant.Constants;
import com.knugpt.knugpt.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        request.setAttribute(Constants.USER_ID_ATTRIBUTE_NAME, userDetails.getUsername());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
