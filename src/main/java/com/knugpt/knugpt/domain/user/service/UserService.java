package com.knugpt.knugpt.domain.user.service;

import com.knugpt.knugpt.domain.user.dto.request.UserInfoUpdateRequest;
import com.knugpt.knugpt.domain.user.dto.response.UserInfoResponse;
import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.domain.user.repository.UserRepository;
import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserInfoResponse getUserInfos(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserInfoResponse.fromEntity(user);
    }

    public void updateUserInfos(Long userId, UserInfoUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        user.updateFromRequest(request);

        userRepository.save(user);
    }
}
