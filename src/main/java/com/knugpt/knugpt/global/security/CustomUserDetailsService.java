package com.knugpt.knugpt.global.security;

import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.domain.user.repository.UserRepository;
import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return CustomUserDetails.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .authorities(
                Collections.singletonList(
                    new SimpleGrantedAuthority(
                        user.getRole().getAuthority()
                    )
                )
            )
            .build();
    }
}
