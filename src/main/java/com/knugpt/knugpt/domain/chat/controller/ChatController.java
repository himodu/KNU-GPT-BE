package com.knugpt.knugpt.domain.chat.controller;

import com.knugpt.knugpt.domain.chat.dto.request.ChatQueryRequest;
import com.knugpt.knugpt.domain.chat.dto.response.AnswerChatResponse;
import com.knugpt.knugpt.domain.chat.dto.response.ChatListResponse;
import com.knugpt.knugpt.domain.chat.service.ChatService;
import com.knugpt.knugpt.global.annotation.UserId;
import com.knugpt.knugpt.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @Operation(
            summary = "[회원] 챗봇에게 채팅을 전송합니다.",
            description = "[회원] 챗봇에게 채팅을 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "챗봇에게 응답 수신 성공"),
            }
    )
    @PostMapping("/chat-rooms/{chatRoomId}/chats")
    public ResponseDto<AnswerChatResponse> queryToChatBotByUser(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable("chatRoomId") Long chatRoomId,
            @Valid @RequestBody ChatQueryRequest request
    ) {
        return ResponseDto.ok(chatService.queryToChatBotByUser(userId, chatRoomId, request));
    }

    @Operation(
            summary = "[비회원] 챗봇에게 채팅을 전송합니다.",
            description = "[비회원] 챗봇에게 채팅을 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "챗봇에게 응답 수신 성공"),
            }
    )
    @PostMapping("/chats")
    public ResponseDto<AnswerChatResponse> queryToChatBot(
            @Valid @RequestBody ChatQueryRequest request
    ) {
        return ResponseDto.ok(chatService.queryToChatBotByNotUser(request));
    }


    @Operation(
            summary = "채팅방 채팅 목록 조회",
            description = "채팅방 채팅 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자별 채팅방 목록 조회 성공"),
            }
    )
    @GetMapping("/chat-rooms/{chatRoomId}/chats")
    public ResponseDto<ChatListResponse> getChats(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        return ResponseDto.ok(chatService.getChats(userId, chatRoomId, page, size));
    }



}
