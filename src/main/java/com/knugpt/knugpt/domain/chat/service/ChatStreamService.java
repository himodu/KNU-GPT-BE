package com.knugpt.knugpt.domain.chat.service;

import com.knugpt.knugpt.domain.chat.dto.request.ChatQueryRequest;
import com.knugpt.knugpt.domain.chat.dto.response.ChatStreamResponse;
import com.knugpt.knugpt.domain.chat.mongo.Chat;
import com.knugpt.knugpt.domain.chat.repository.ChatRepository;
import com.knugpt.knugpt.domain.chatRoom.entity.ChatRoom;
import com.knugpt.knugpt.domain.chatRoom.repository.ChatRoomRepository;
import com.knugpt.knugpt.domain.llm.LlmClient;
import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatStreamService {

    private static final String LLM_SERVER_ERROR_MESSAGE = "죄송합니다. 일시적인 오류로 답변을 생성하지 못했습니다.";

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    private final LlmClient llmClient;

    /**
     * 사용자의 질문, 해당 채팅방 내역과 사용자 정보를 모두 종합하여
     * LLM 챗봇의 응답을 스트리밍으로 응답
     *
     * @param userId
     * @param chatRoomId
     * @param request
     * @return
     */
    public Flux<ServerSentEvent<ChatStreamResponse>> queryToChatBotByUserWithStream(
            Long userId,
            Long chatRoomId,
            ChatQueryRequest request
    ) {
        // 1. 질문, 응답 버퍼 초기화
        String question = request.question();
        StringBuilder fullAnswer = new StringBuilder();

        // 2. 채팅방 존재 여부 확인
        Mono<ChatRoom> chatRoomMono = Mono.fromCallable(() -> chatRoomRepository.findByIdAndUserId(chatRoomId, userId)
                                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CHAT_ROOM))
                )
                .subscribeOn(Schedulers.boundedElastic());

        // 3. 사용자 질문 채팅 저장
        Mono<Void> saveQuestionMono = Mono.fromRunnable(() -> chatRepository.saveUserQuestion(userId, chatRoomId, question))
                .subscribeOn(Schedulers.boundedElastic())
                .then();

        // 4. 최근 5개의 채팅을 불러오기
        Mono<List<Chat>> recentChatsMono = Mono.fromCallable(() -> chatRepository.getRecentChats(chatRoomId, 5))
                .subscribeOn(Schedulers.boundedElastic());

        // 5. 최종 답변 응답
        return chatRoomMono
                .zipWith(saveQuestionMono.then(recentChatsMono))// 질문 저장, 최신 채팅 조회 순차적으로 연결
                .flatMapMany(tuple -> { // Mono 의 결과를 하나의 Flux 로 변환
                    ChatRoom chatRoom = tuple.getT1();
                    List<Chat> recentChats = tuple.getT2();
                    User user = chatRoom.getUser();

                    // 응답 시작 스트리밍 이벤트 발행
                    Flux<ServerSentEvent<ChatStreamResponse>> startFlux = Flux.just(
                            ServerSentEvent
                                    .builder(ChatStreamResponse.start())
                                    .build()
                    );

                    // 응답 내용 스트리밍 이벤트 발행
                    Flux<ServerSentEvent<ChatStreamResponse>> chunkFlux = llmClient.queryToChatBotWithUserInfoStream(
                                            user,
                                            recentChats.stream()
                                                    .sorted(Comparator.comparing(Chat::getCreatedAt))
                                                    .toList(),
                                            question
                                    )
                                    .doOnNext(fullAnswer::append)
                                    .map(chunk -> ServerSentEvent.builder(
                                            ChatStreamResponse.chunk(chunk)
                                    ).build());

                    // 응답 종료 스트리밍 이벤트 발행
                    Mono<ServerSentEvent<ChatStreamResponse>> endMono = Mono.fromCallable(() -> {
                                        String finalAnswer = fullAnswer.isEmpty()
                                                ? LLM_SERVER_ERROR_MESSAGE
                                                : fullAnswer.toString();

                                        chatRepository.saveChatbotAnswer(chatRoomId, finalAnswer);

                                        return ServerSentEvent.builder(ChatStreamResponse.end()).build();
                                    })
                                    .subscribeOn(Schedulers.boundedElastic());

                    // 결과 발행
                    return startFlux
                            .concatWith(chunkFlux)
                            .concatWith(endMono);
                })
                .onErrorResume( // 에러 처리
                        e -> Mono.fromCallable(() -> {
                                    String finalAnswer = fullAnswer.isEmpty()
                                            ? LLM_SERVER_ERROR_MESSAGE
                                            : fullAnswer.toString();

                                    chatRepository.save(Chat.chatbotOf(chatRoomId, finalAnswer));

                                    return ServerSentEvent.builder(
                                            ChatStreamResponse.error("답변 생성 중 오류가 발생했습니다.")
                                    ).build();
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                                .flux()
                );
    }

    /**
     * 사용자에 질문에 대한 LLM 챗봇의 응답을
     * 스트리밍으로 응답
     *
     * @param request
     * @return
     */
    public Flux<ServerSentEvent<ChatStreamResponse>> queryToChatBotByNotUserWithStream(ChatQueryRequest request) {
        // 1. 질문 초기화
        String question = request.question();

        // 2. 응답 시작 스트리밍 이벤트 발행
        Flux<ServerSentEvent<ChatStreamResponse>> startFlux = Flux.just(
                ServerSentEvent
                        .builder(ChatStreamResponse.start())
                        .build()
        );

        // 3. 응답 내용 스트리밍 이벤트 발행
        Flux<ServerSentEvent<ChatStreamResponse>> chunkFlux = llmClient.queryToChatBotWithoutUserInfoStream(question)
                .map(chunk -> ServerSentEvent.builder(
                        ChatStreamResponse.chunk(chunk)
                ).build());

        // 4. 응답 종료 스트리밍 이벤트 발행
        Mono<ServerSentEvent<ChatStreamResponse>> endMono = Mono.fromCallable(() -> ServerSentEvent.builder(ChatStreamResponse.end()).build())
                        .subscribeOn(Schedulers.boundedElastic());

        // 5. 시작, 내용, 종료 순차적으로 응답
        return startFlux
                .concatWith(chunkFlux)
                .concatWith(endMono)
                .onErrorResume(e -> Mono.just(
                                ServerSentEvent.builder(ChatStreamResponse.error("답변 생성 중 오류가 발생했습니다.")).build()
                        )
                );
    }

}
