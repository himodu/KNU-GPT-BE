package com.knugpt.knugpt.domain.auth.dto.response;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record TokenRefreshResponse(
    @NonNull String accessToken
) {

}
