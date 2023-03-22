package com.dnlab.tacktogetherbackend.user.service;

import com.dnlab.tacktogetherbackend.user.common.MemberAuthority;
import com.dnlab.tacktogetherbackend.user.common.MemberRegistrationDTO;
import com.dnlab.tacktogetherbackend.user.common.MemberResponseDTO;
import com.dnlab.tacktogetherbackend.user.domain.Authority;
import com.dnlab.tacktogetherbackend.user.domain.Member;
import com.dnlab.tacktogetherbackend.user.repository.AuthorityRepository;
import com.dnlab.tacktogetherbackend.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.users.MemoryRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberResponseDTO registerMember(MemberRegistrationDTO registrationDTO) {
        Member member = Member.builder()
                .username(registrationDTO.getUsername())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .enabled(true)
                .build();
        member = memberRepository.save(member);

        authorityRepository.save(Authority.builder()
                .member(member)
                .authority(MemberAuthority.ROLE_MEMBER.toString())
                .build());

        return MemberResponseDTO.builder().username(member.getUsername()).build();
    }

    @Override
    public void validateDuplicated(String username) {

    }
}
