package com.knugpt.knugpt.domain.chat.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatType {
    USER("USER"),
    CHATBOT("CHATBOT")
    ;

    private final String value;
}
