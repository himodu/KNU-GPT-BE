package com.knugpt.knugpt.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    GRADE1("GRADE1"),
    GRADE2("GRADE2"),
    GRADE3("GRADE3"),
    GARDE4("GRADE4"),
    BREAKE("BRAKE"),
    EXCEED("EXCEED"),
    DEFERRED("DEFERRED"),
    GRADUATESCHOOL("GRADUATESCHOOL"),
    STAFF("STAFF"),
    NONE("NONE");


    private final String value;
}
