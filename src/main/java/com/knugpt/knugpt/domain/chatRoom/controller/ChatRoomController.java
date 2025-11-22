package com.knugpt.knugpt.domain.chatRoom.controller;


import com.knugpt.knugpt.domain.chatRoom.dto.request.ChatRoomCreateRequest;
import com.knugpt.knugpt.domain.chatRoom.dto.request.ChatRoomTitleUpdateRequest;
import com.knugpt.knugpt.domain.chatRoom.dto.response.ChatRoomCreatedResponse;
import com.knugpt.knugpt.domain.chatRoom.dto.response.ChatRoomListResponse;
import com.knugpt.knugpt.domain.chatRoom.service.ChatRoomService;
import com.knugpt.knugpt.global.annotation.UserId;
import com.knugpt.knugpt.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(
            summary = "채팅방 생성",
            description = "채팅방을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "사용자 정보 조회 성공"),
            }
    )
    @PostMapping("")
    public ResponseDto<ChatRoomCreatedResponse> getUserInfos(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid @RequestBody ChatRoomCreateRequest request
    ) {
        return ResponseDto.created(chatRoomService.createChatRoom(userId, request));
    }


    @Operation(
            summary = "채팅방 목록 조회",
            description = "사용자 별 채팅방 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자별 채팅방 목록 조회 성공"),
            }
    )
    @GetMapping("")
    public ResponseDto<ChatRoomListResponse> getChatRooms(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return ResponseDto.ok(chatRoomService.getChatRooms(userId, page, size));
    }


    @Operation(
            summary = "채팅방 제목 수정",
            description = "채팅방 제목을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자별 채팅방 목록 조회 성공"),
            }
    )
    @PatchMapping("/{chatRoomId}")
    public ResponseDto<Void> updateChatRoomTitle(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable("chatRoomId") Long chatRoomId,
            @Valid @RequestBody ChatRoomTitleUpdateRequest request
    ) {
        chatRoomService.updateChatRoomTitle(userId, chatRoomId, request);
        return ResponseDto.ok(null);
    }


    @Operation(
            summary = "채팅방 삭제",
            description = "채팅방을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "채팅방을 삭제합니다."),
            }
    )
    @DeleteMapping("/{chatRoomId}")
    public ResponseDto<Void> deleteChatRoom(
            @Parameter(hidden = true) @UserId Long userId,
            @PathVariable("chatRoomId") Long chatRoomId
    ) {
        chatRoomService.deleteChatRoom(userId, chatRoomId);
        return ResponseDto.noContent();
    }
}
