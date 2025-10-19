package com.knugpt.knugpt.domain.chat.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatType {
    QUESTION("QUESTION"),
    ANSWER("ANSWER")
    ;

    private final String value;
}
