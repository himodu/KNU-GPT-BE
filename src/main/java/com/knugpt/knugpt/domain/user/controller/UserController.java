package com.knugpt.knugpt.domain.user.controller;

import com.knugpt.knugpt.domain.user.dto.request.UserInfoUpdateRequest;
import com.knugpt.knugpt.domain.user.dto.response.UserInfoResponse;
import com.knugpt.knugpt.domain.user.service.UserService;
import com.knugpt.knugpt.global.annotation.UserId;
import com.knugpt.knugpt.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "사용자 개인정보 조회",
            description = "사용자 개인정보 조회입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            }
    )
    @GetMapping("/infos")
    public ResponseDto<UserInfoResponse> getUserInfos(
            @Parameter(hidden = true) @UserId Long userId
    ) {
        return ResponseDto.ok(userService.getUserInfos(userId));
    }

    @Operation(
            summary = "사용자 개인정보 수정",
            description = "사용자 개인정보 수정입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
            }
    )
    @PutMapping("/infos")
    public ResponseDto<Void> updateUserInfos(
            @Parameter(hidden = true) @UserId Long userId,
            @Valid@RequestBody UserInfoUpdateRequest request
    ) {
        userService.updateUserInfos(userId, request);
        return ResponseDto.ok(null);
    }
}
