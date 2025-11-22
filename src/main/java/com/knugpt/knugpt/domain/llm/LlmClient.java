package com.knugpt.knugpt.domain.llm;

import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.domain.llm.dto.request.LlmChatBotQueryRequest;
import com.knugpt.knugpt.domain.llm.dto.response.LlmChatBotAnswerResponse;
import com.knugpt.knugpt.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.knugpt.knugpt.global.exception.ExceptionUtil.getSimpleName;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmClient {
    @Value("${llm.server.host}")
    private String aiServerHost;
    private final WebClient webClient;
    private final static String CHAT_PATH = "/chat";
    private final static String TITLE_SUMMARIZE_PATH = "/title";

    public String queryToChatBotWithUserInfo(User user, List<Chat> chats, String question) {
        // 1. 회원 전용 요청 객체 생성
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.of(user, chats, question);

        // 2. LLM 서버 호출 및 반환
        return sendQueryToChatBot(request, CHAT_PATH);
    }

    public String queryToChatBotWithoutUserInfo(String question) {
        // 1. 비회원 전용 요청 객체 생성
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.onlyQuestionOf(question);

        // 2. LLM 서버 호출 및 반환
        return sendQueryToChatBot(request, CHAT_PATH);
    }

    public String summarizeQueryForTitle(String question) {
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.onlyQuestionOf(question);

        // 2. LLM 서버 호출 및 반환
        return sendQueryToChatBot(request, TITLE_SUMMARIZE_PATH);
    }

    public String sendQueryToChatBot(Object request, String uriPath) {
        try {
            return webClient.post()
                    .uri(aiServerHost + uriPath)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(LlmChatBotAnswerResponse.class)
                    .map(LlmChatBotAnswerResponse::answer)
                    .block();
        } catch (Exception e) {
            log.error("LLM 서버 연결 도중 {} 를 반환 : {}", getSimpleName(e), e.getMessage());
        }
        return null;
    }

}
