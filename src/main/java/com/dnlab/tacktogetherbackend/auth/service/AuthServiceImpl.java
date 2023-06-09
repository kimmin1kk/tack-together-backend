package com.dnlab.tacktogetherbackend.auth.service;

import com.dnlab.tacktogetherbackend.auth.common.JwtTokenProvider;
import com.dnlab.tacktogetherbackend.auth.domain.redis.RefreshToken;
import com.dnlab.tacktogetherbackend.auth.dto.*;
import com.dnlab.tacktogetherbackend.auth.common.Role;
import com.dnlab.tacktogetherbackend.auth.domain.Authority;
import com.dnlab.tacktogetherbackend.auth.domain.Member;
import com.dnlab.tacktogetherbackend.auth.exception.DuplicateUsernameException;
import com.dnlab.tacktogetherbackend.auth.exception.TokenNotFoundException;
import com.dnlab.tacktogetherbackend.auth.repository.AuthorityRepository;
import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.auth.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public LoginResponseDTO signIn(LoginRequestDTO loginRequestDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        Set<RefreshToken> tokens = tokenRepository.findRefreshTokensByUsername(authentication.getName());
        log.debug("infos: " + tokens);
        tokenRepository.deleteAll(tokens);
        RefreshToken tokenInfo = new RefreshToken(refreshToken, authentication.getName());
        tokenRepository.save(tokenInfo);

        return LoginResponseDTO.builder()
                .username(authentication.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public RegistrationResponseDTO signUp(RegistrationRequestDTO registrationDTO) {
        if (memberRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new DuplicateUsernameException("이미 존재하는 username");
        }

        Member member = Member.builder()
                .username(registrationDTO.getUsername())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .name(registrationDTO.getName())
                .nickname(registrationDTO.getNickname())
                .enabled(true)
                .build();
        member = memberRepository.save(member);

        Authority authority = authorityRepository.save(Authority.builder()
                .member(member)
                .authorityName(Role.ROLE_MEMBER)
                .build());

        return RegistrationResponseDTO.of(member, authority);
    }

    @Override
    public LoginResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        String refreshToken = refreshTokenRequestDTO.getRefreshToken();
        jwtTokenProvider.validateToken(refreshToken); // 유효성 검사

        RefreshToken tokenInfo = tokenRepository.findById(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token not found"));

        tokenRepository.delete(tokenInfo);

        String newRefreshToken = jwtTokenProvider.createRefreshToken();
        tokenInfo.setToken(newRefreshToken);
        tokenRepository.save(tokenInfo);

        return LoginResponseDTO.builder()
                .username(tokenInfo.getUsername())
                .accessToken(jwtTokenProvider.createAccessToken(new UsernamePasswordAuthenticationToken(tokenInfo.getUsername(), null, new ArrayList<>())))
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public boolean validAuthentication(HttpServletRequest request) {
        return jwtTokenProvider.validateToken(jwtTokenProvider.resolveToken(request));
    }

    @Override
    public MemberInfoResponseDTO getMemberInfoByUsername(String username) {
        return MemberInfoResponseDTO.of(memberRepository.findMemberByUsername(username).orElseThrow());
    }

    @Override
    public CheckUsernameRequestDTO checkAvailableUsername(String username) {
        if (username.length() < 5 || username.length() > 20) {
            return new CheckUsernameRequestDTO(false);
        }
        return new CheckUsernameRequestDTO(!memberRepository.existsByUsername(username));
    }

    @Override
    @Transactional
    public MemberUpdateDTO updateMemberInfo(MemberUpdateDTO memberUpdateDTO, String username) {
        Member member = memberRepository.findMemberByUsername(username).orElseThrow();

        if (memberUpdateDTO.getName() != null) {
            member.setName(memberUpdateDTO.getName());
        }

        if (memberUpdateDTO.getNickname() != null) {
            member.setNickname(memberUpdateDTO.getNickname());
        }

        if (memberUpdateDTO.getPassword() != null) {
            member.setPassword(passwordEncoder.encode(memberUpdateDTO.getPassword()));
        }

        return MemberUpdateDTO.of(member);
    }

    @Override
    public void logout(String username) {
        tokenRepository.deleteAll(tokenRepository.findRefreshTokensByUsername(username));
    }
}
