package com.dgsw.cns.clubinsearch.domain.auth.service;

import com.dgsw.cns.clubinsearch.domain.user.domain.enums.Role;
import com.dgsw.cns.clubinsearch.domain.user.exception.ExistsUserEmailException;
import com.dgsw.cns.clubinsearch.domain.auth.presentation.dto.request.CreateUserRequest;
import com.dgsw.cns.clubinsearch.domain.auth.presentation.dto.request.LoginUserRequest;
import com.dgsw.cns.clubinsearch.domain.auth.presentation.dto.response.LoginResponse;
import com.dgsw.cns.clubinsearch.domain.user.domain.User;
import com.dgsw.cns.clubinsearch.domain.user.domain.repository.UserRepository;
import com.dgsw.cns.clubinsearch.domain.user.exception.NotFoundAccountIdException;
import com.dgsw.cns.clubinsearch.domain.user.exception.NotFoundUserEmailException;
import com.dgsw.cns.clubinsearch.domain.user.exception.NotMatchesPasswordException;
import com.dgsw.cns.clubinsearch.global.secirity.jwt.JwtTokenProvider;
import com.dgsw.cns.clubinsearch.global.secirity.jwt.enums.JwtType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public void createUser(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw ExistsUserEmailException.EXCEPTION;
        }

        userRepository.save(
                User.builder()
                        .accountId(request.getAccountId())
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.ROLE_ADMIN)
                        .build()
        );
    }

    public LoginResponse loginUser(LoginUserRequest request) {
        User user = userRepository.findByAccountId(request.getAccountId())
                .orElseThrow(() -> NotFoundAccountIdException.EXCEPTION);

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw NotMatchesPasswordException.EXCEPTION;
        }

        return new LoginResponse(
                jwtTokenProvider.createToken(request.getAccountId(), JwtType.ACCESS),
                jwtTokenProvider.createToken(request.getAccountId(), JwtType.REFRESH)
        );
    }

}
