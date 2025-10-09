package com.knugpt.knugpt.global.interceptor.pre;

import com.knugpt.knugpt.global.annotation.UserId;
import com.knugpt.knugpt.global.constant.Constants;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Long.class)
                && parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        final String userIdObj = (String) webRequest.getAttribute(
                Constants.USER_ID_ATTRIBUTE_NAME,
                WebRequest.SCOPE_REQUEST
        );

        return Long.parseLong(userIdObj);
    }
}
