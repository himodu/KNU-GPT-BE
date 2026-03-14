package com.knugpt.knugpt.global.exception;

import java.util.Arrays;
import java.util.List;

public class ExceptionUtil {

    public static String getMethodName(Throwable e) {
        List<StackTraceElement> stackTrace = Arrays.stream(e.getStackTrace()).toList();
        StackTraceElement errorElement = stackTrace.stream()
                .filter(element -> element.getClassName().startsWith("com.hakwonsin"))// 작동한 service 메서드
                .findFirst()
                .orElseGet(() -> stackTrace.get(0));

        return errorElement.getMethodName();
    }

    public static String getLineNumber(Throwable e) {
        List<StackTraceElement> stackTrace = Arrays.stream(e.getStackTrace()).toList();
        StackTraceElement errorElement = stackTrace.stream()
                .filter(element -> element.getClassName().startsWith("com.hakwonsin"))// 작동한 service 메서드
                .findFirst()
                .orElseGet(() -> stackTrace.get(0));

        return errorElement.getFileName()+":"+errorElement.getLineNumber();
    }

    public static String getSimpleName(Throwable e) {
        return e.getClass().getSimpleName();
    }
}
