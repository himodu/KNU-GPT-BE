package com.knugpt.knugpt.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    GRADE1("GRADE1", "1학년 재학"),
    GRADE2("GRADE2", "2학년 재학"),
    GRADE3("GRADE3", "3학년 재학"),
    GARDE4("GRADE4", "4학년 재학"),
    BREAKE("BRAKE", "휴학 중"),
    EXCEED("EXCEED", "초과 학기"),
    DEFERRED("DEFERRED", "졸업 유예"),
    GRADUATESCHOOL("GRADUATESCHOOL", "졸업생"),
    STAFF("STAFF", "교직원"),
    NONE("NONE", "아무것도 아님");


    private final String value;
    private final String language;
}
