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

    public Flux<ServerSentEvent<ChatStreamResponse>> queryToChatBotByUserWithStream(
            Long userId,
            Long chatRoomId,
            ChatQueryRequest request
    ) {
        String question = request.question();
        StringBuilder fullAnswer = new StringBuilder();

        Mono<ChatRoom> chatRoomMono = Mono.fromCallable(() ->
                        chatRoomRepository.findByIdAndUserId(chatRoomId, userId)
                                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_CHAT_ROOM))
                )
                .subscribeOn(Schedulers.boundedElastic());

        Mono<Void> saveQuestionMono = Mono.fromRunnable(() ->
                        chatRepository.saveUserQuestion(userId, chatRoomId, question)
                )
                .subscribeOn(Schedulers.boundedElastic())
                .then();

        Mono<List<Chat>> recentChatsMono = Mono.fromCallable(() ->
                        chatRepository.getRecentChats(chatRoomId, 5)
                )
                .subscribeOn(Schedulers.boundedElastic());

        return chatRoomMono
                .zipWith(saveQuestionMono.then(recentChatsMono))
                .flatMapMany(tuple -> {
                    ChatRoom chatRoom = tuple.getT1();
                    List<Chat> recentChats = tuple.getT2();
                    User user = chatRoom.getUser();

                    Flux<ServerSentEvent<ChatStreamResponse>> startFlux = Flux.just(
                            ServerSentEvent
                                    .builder(ChatStreamResponse.start())
                                    .build()
                    );

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

                    Mono<ServerSentEvent<ChatStreamResponse>> endMono =
                            Mono.fromCallable(() -> {
                                        String finalAnswer = fullAnswer.isEmpty()
                                                ? LLM_SERVER_ERROR_MESSAGE
                                                : fullAnswer.toString();

                                        chatRepository.saveChatbotAnswer(chatRoomId, finalAnswer);

                                        return ServerSentEvent.builder(ChatStreamResponse.end()).build();
                                    })
                                    .subscribeOn(Schedulers.boundedElastic());

                    return startFlux
                            .concatWith(chunkFlux)
                            .concatWith(endMono);
                })
                .onErrorResume(e -> Mono.fromCallable(() -> {
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


    public Flux<ServerSentEvent<ChatStreamResponse>> queryToChatBotByNotUserWithStream(ChatQueryRequest request) {
        String question = request.question();

        Flux<ServerSentEvent<ChatStreamResponse>> startFlux = Flux.just(
                ServerSentEvent
                        .builder(ChatStreamResponse.start())
                        .build()
        );

        Flux<ServerSentEvent<ChatStreamResponse>> chunkFlux = llmClient.queryToChatBotWithoutUserInfoStream(question)
                .map(chunk -> ServerSentEvent.builder(
                        ChatStreamResponse.chunk(chunk)
                ).build());

        Mono<ServerSentEvent<ChatStreamResponse>> endMono = Mono.fromCallable(() -> ServerSentEvent.builder(ChatStreamResponse.end()).build())
                        .subscribeOn(Schedulers.boundedElastic());

        return startFlux
                .concatWith(chunkFlux)
                .concatWith(endMono)
                .onErrorResume(e -> Mono.just(
                                ServerSentEvent.builder(ChatStreamResponse.error("답변 생성 중 오류가 발생했습니다.")).build()
                        )
                );


    }

}
