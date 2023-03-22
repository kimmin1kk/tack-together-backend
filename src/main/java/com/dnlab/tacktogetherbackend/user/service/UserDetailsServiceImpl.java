package com.dnlab.tacktogetherbackend.user.service;

import com.dnlab.tacktogetherbackend.user.common.UserDetailsImpl;
import com.dnlab.tacktogetherbackend.user.domain.Member;
import com.dnlab.tacktogetherbackend.user.repository.AuthorityRepository;
import com.dnlab.tacktogetherbackend.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByUsername(username).orElseThrow();
        return authorityRepository.findAuthoritiesByMember(member)
                .map(authorities -> new UserDetailsImpl(member, authorities))
                .orElse(null);
    }
}
