package com.dnlab.tacktogetherbackend.auth.service;

import com.dnlab.tacktogetherbackend.auth.common.JwtTokenProvider;
import com.dnlab.tacktogetherbackend.auth.dto.*;
import com.dnlab.tacktogetherbackend.auth.common.Role;
import com.dnlab.tacktogetherbackend.auth.domain.Authority;
import com.dnlab.tacktogetherbackend.auth.domain.Member;
import com.dnlab.tacktogetherbackend.auth.exception.DuplicateUsernameException;
import com.dnlab.tacktogetherbackend.auth.repository.AuthorityRepository;
import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    @Override
    @Transactional
    public LoginResponseDTO signIn(LoginRequestDTO loginRequestDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        memberRepository.findMemberByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없음"))
                .setRefreshToken(refreshToken);

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
    @Transactional
    public LoginResponseDTO refreshAccessToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        String refreshToken = refreshTokenRequestDTO.getRefreshToken();
        jwtTokenProvider.validateToken(refreshToken); // 유효성 검사

        Member member = memberRepository.findMemberByRefreshToken(refreshToken).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없음"));

        String newRefreshToken = jwtTokenProvider.createRefreshToken();
        member.setRefreshToken(newRefreshToken);

        return LoginResponseDTO.builder()
                .username(member.getUsername())
                .accessToken(jwtTokenProvider.createAccessToken(new UsernamePasswordAuthenticationToken(member.getUsername(), null, new ArrayList<>())))
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
    public CheckUsernameRequestDTO checkDuplicatedUsername(String username) {
        return new CheckUsernameRequestDTO(memberRepository.existsByUsername(username));
    }
}
