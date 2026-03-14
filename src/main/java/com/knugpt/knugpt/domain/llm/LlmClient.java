package com.knugpt.knugpt.domain.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.domain.llm.dto.request.LlmChatBotQueryRequest;
import com.knugpt.knugpt.domain.llm.dto.response.LlmChatBotAnswerResponse;
import com.knugpt.knugpt.domain.llm.dto.response.LlmChatBotAnswerStreamChunkResponse;
import com.knugpt.knugpt.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.knugpt.knugpt.global.exception.ExceptionUtil.getSimpleName;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmClient {
    @Value("${llm.server.host}")
    private String aiServerHost;
    private final static String CHAT_PATH = "/chat";
    private final static String TITLE_SUMMARIZE_PATH = "/title";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public String queryToChatBotWithUserInfo(User user, List<Chat> chats, String question) {
        // 1. 회원 전용 요청 객체 생성
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.of(user, chats, question, false);

        // 2. LLM 서버 호출 및 반환
        return sendQueryToChatBot(request, CHAT_PATH);
    }

    public Flux<String> queryToChatBotWithUserInfoStream(User user, List<Chat> chats, String question) {
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.of(user, chats, question, true);
        return sendQueryToChatBotWithStream(request, CHAT_PATH);
    }

    public String queryToChatBotWithoutUserInfo(String question) {
        // 1. 비회원 전용 요청 객체 생성
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.onlyQuestionOf(question, false);

        // 2. LLM 서버 호출 및 반환
        return sendQueryToChatBot(request, CHAT_PATH);
    }

    public Flux<String> queryToChatBotWithoutUserInfoStream(String question) {
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.onlyQuestionOf(question, true);
        return sendQueryToChatBotWithStream(request, CHAT_PATH);
    }

    public String summarizeQueryForTitle(String question) {
        LlmChatBotQueryRequest request = LlmChatBotQueryRequest.onlyQuestionOf(question, false);

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

    public Flux<String> sendQueryToChatBotWithStream(Object request, String uriPath) {
        return webClient.post()
                .uri(aiServerHost + uriPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                .doOnSubscribe(s -> log.info("LLM 스트리밍 요청 시작: uri={}", aiServerHost + uriPath))
                .doOnNext(event -> log.info(
                        "SSE 수신 event={}, id={}, data={}",
                        event.event(),
                        event.id(),
                        event.data()
                ))
                .flatMap(event -> {
                    String data = event.data();
                    if (data == null || data.isBlank()) {
                        return Flux.empty();
                    }

                    try {
                        LlmChatBotAnswerStreamChunkResponse response = objectMapper.readValue(data, LlmChatBotAnswerStreamChunkResponse.class);

                        if ("chunk".equals(response.type())) {
                            return Flux.just(response.content() == null ? "" : response.content());
                        }

                        if ("end".equals(response.type())) {
                            return Flux.empty();
                        }

                        if ("error".equals(response.type())) {
                            String message = response.message() == null
                                    ? "LLM 서버 스트리밍 중 오류가 발생했습니다."
                                    : response.message();
                            return Flux.error(new RuntimeException(message));
                        }

                        return Flux.empty();
                    } catch (JsonProcessingException e) {
                        log.error("LLM 스트리밍 응답 파싱 실패: {}", e.getMessage());
                        return Flux.error(e);
                    }
                })
                .doOnNext(chunk -> log.info("최종 chunk 방출: {}", chunk))
                .doOnComplete(() -> log.info("클라이언트 스트림 완료"))
                .doOnError(e -> log.error("LLM 서버 스트리밍 도중 {} 를 반환 : {}", getSimpleName(e), e.getMessage()));
    }

}