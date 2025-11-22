package com.knugpt.knugpt.domain.llm;

import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.domain.llm.dto.request.LlmChatBotQueryRequest;
import com.knugpt.knugpt.domain.llm.dto.response.LlmChatBotAnswerResponse;
import com.knugpt.knugpt.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LLmClient {
    @Value("${llm.server.host}")
    private static String aiServerHost;

    private final WebClient webClient;

    public String queryToChatBotWithUserInfo(User user, List<Chat> chats, String question) {
        // 1. 회원 전용 요청 객체 생성
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.of(user, chats, question);

        // 2. LLM 서버 호출 및 반환
        return webClient.post()
                .uri(String.format(aiServerHost, "/search"))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LlmChatBotAnswerResponse.class)
                .map(LlmChatBotAnswerResponse::getAnswer)
                .block();
    }

    public String queryToChatBotWithoutUserInfo(String question) {
        // 1. 비회원 전용 요청 객체 생성
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.onlyQuestionOf(question);

        // 2. LLM 서버 호출 및 반환
        return webClient.post()
                .uri(String.format(aiServerHost, "/search"))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LlmChatBotAnswerResponse.class)
                .map(LlmChatBotAnswerResponse::getAnswer)
                .block();
    }

    public String summarizeQueryForTitle(String question) {
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.onlyQuestionOf(question);

        // 2. LLM 서버 호출 및 반환
        return webClient.post()
                .uri(String.format(aiServerHost, "/title"))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LlmChatBotAnswerResponse.class)
                .map(LlmChatBotAnswerResponse::getAnswer)
                .block();
    }

}
