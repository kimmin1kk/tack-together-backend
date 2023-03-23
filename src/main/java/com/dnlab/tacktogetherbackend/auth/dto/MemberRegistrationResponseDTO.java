package com.dnlab.tacktogetherbackend.auth.dto;

import com.dnlab.tacktogetherbackend.auth.domain.Authority;
import com.dnlab.tacktogetherbackend.auth.domain.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public class MemberRegistrationResponseDTO {
    private String username;
    private String password;
    private Set<String> authorities;

    public static MemberRegistrationResponseDTO of(Member member, Authority authority) {
        if (member == null) return null;

        return MemberRegistrationResponseDTO.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .authorities(Stream.of(authority).map(auth -> auth.getAuthorityName().toString()).collect(Collectors.toSet()))
                .build();
    }

    @Builder
    public MemberRegistrationResponseDTO(String username, String password, Set<String> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }
}
