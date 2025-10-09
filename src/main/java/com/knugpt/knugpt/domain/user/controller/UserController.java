package com.knugpt.knugpt.domain.user.controller;

import com.knugpt.knugpt.global.annotation.UserId;
import com.knugpt.knugpt.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    @Operation(
            summary = "UserId 어노테이션 테스트",
            description = "UserId 어노테이션 테스트입니다. @UserId 어노테이션이면 User 의 PK 를 쉽게 조회할 수 있습니다. 제 선물입니다..ㅎㅎ",
            responses = {
                    @ApiResponse(responseCode = "200", description = " UserId 추출 성공 "),
            }
    )
    @GetMapping("/userId-test")
    public ResponseDto<String> signup(
            @Parameter(hidden = true) @UserId Long userId
    ) {
        return ResponseDto.ok("USER_ID : " + userId.toString());
    }
}
