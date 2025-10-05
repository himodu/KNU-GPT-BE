package com.knugpt.knugpt.domain.auth.service;

import com.knugpt.knugpt.domain.auth.dto.request.EmailSendRequest;
import com.knugpt.knugpt.domain.auth.dto.request.EmailValidateRequest;
import com.knugpt.knugpt.domain.auth.dto.request.UserLoginRequest;
import com.knugpt.knugpt.domain.auth.dto.request.UserSignupRequest;
import com.knugpt.knugpt.domain.auth.dto.response.AccountLoginResponse;
import com.knugpt.knugpt.domain.auth.dto.response.TokenRefreshResponse;
import com.knugpt.knugpt.domain.auth.redis.EmailValidation;
import com.knugpt.knugpt.domain.auth.repository.AuthRepository;
import com.knugpt.knugpt.domain.auth.repository.EmailValidationRepository;
import com.knugpt.knugpt.domain.user.entity.Provider;
import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.domain.user.entity.UserRole;
import com.knugpt.knugpt.domain.user.repository.UserRepository;
import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import com.knugpt.knugpt.global.security.CustomUserDetails;
import com.knugpt.knugpt.global.security.jwt.JwtTokenProvider;
import com.knugpt.knugpt.global.util.EmailUtil;
import com.knugpt.knugpt.global.util.PasswordUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthService{

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final EmailValidationRepository emailValidationRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final EmailUtil emailUtil;

    public void sendEmailValidateCode(EmailSendRequest request) {
        //1. 인증코드 생성
        String validationCode = PasswordUtil.generateAuthCode(4);

        //2. 인증코드 메일로 발송
        emailUtil.sendEmailValidationMail(request.email(), validationCode);

        //3. 이메일, 인증코드 임시 저장
        emailValidationRepository.save(EmailValidation.of(request.email(), validationCode));
    }

    public void validateEmail(EmailValidateRequest request) {
        //1. 인증 임시 저장값 조회
        EmailValidation validation = emailValidationRepository.findById(request.email())
                .orElseThrow(() -> new CommonException(ErrorCode.EMAIL_NOT_VALID));

        // 인증 횟수가 기준을 초과할 경우 예외 반환
        if (validation.isOverLimit()) {
            throw new CommonException(ErrorCode.EMAIL_VALIDATE_LIMIT_OVER);
        }

        //2. 인증 임시 저장값에 성공 여부 갱신
        if (isValidEmail(request.code(), validation)) {
            validation.validationUpdate(true);

        } else {
            // 인증 시도 횟수 +1
            emailValidationRepository.save(validation.addTryCnt());

            // 이메일 인증코드 일치하지 않음
            throw new CommonException(ErrorCode.EMAIL_CODE_NOT_VALID);
        }
    }

    private boolean isValidEmail(String code, EmailValidation validation) {
        return code.equals(validation.getCode());
    }

    public void signup(UserSignupRequest request) {
        //1. 인증 임시 저장값의 이메일과 대조 후 임시 저장값 삭제
        //TODO - 로직 구현 해야됨

        //2. 회원 정보 생성 및 저장
        userRepository.save(
                User.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .nickname(request.nickname())
                        .profileImageUrl(request.profileImageLink())
                        .role(UserRole.USER)
                        .provider(Provider.KAKAO)
                        .build()
                );
    }

    public AccountLoginResponse login(UserLoginRequest loginRequestDto) {

        // 인증
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDto.email(),
                loginRequestDto.password()
            )
        );
        log.info("인증 완료: {}", authentication.getName());

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // Refresh 토큰 저장
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long accountId = customUserDetails.getUserId();
        authRepository.saveRefreshToken(
            accountId,
            refreshToken,
            JwtTokenProvider.REFRESH_TOKEN_VALIDITY
        );

        String userRole = customUserDetails.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse(null); // 권한이 없는 경우 null 반환
        return AccountLoginResponse.builder()
            .accountId(accountId)
            .userRole(userRole)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    @Transactional(readOnly = true)
    public TokenRefreshResponse getNewAccessToken(String refreshToken) {
        // refresh 토큰 유효성 검사
        Claims claims = jwtTokenProvider.getClaimsFromToken(refreshToken); // throws jwtException
        Long accountId = claims.get(JwtTokenProvider.USER_ID_KEY, Long.class);
        String authority = claims.get(JwtTokenProvider.AUTHORIZATION_KEY, String.class);

        // 저장된 Refresh 토큰과 비교
        String savedRefreshToken = authRepository.findRefreshTokenByAccountId(accountId)
            .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_END_POINT));
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new CommonException(ErrorCode.NOT_FOUND_END_POINT);
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessTokenWithRefreshTokenInfo(
            accountId,
            authority
        );

        return TokenRefreshResponse.builder()
            .accessToken(newAccessToken)
            .build();
    }

    public void logout(Long accountId) {
        // Refresh Token 삭제
        authRepository.deleteRefreshToken(accountId);
    }
}