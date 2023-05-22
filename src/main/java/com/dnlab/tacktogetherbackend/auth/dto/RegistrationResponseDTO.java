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
public class RegistrationResponseDTO {
    private String username;
    private String password;
    private String name;
    private Set<String> authorities;

    public static RegistrationResponseDTO of(Member member, Authority authority) {
        if (member == null) return null;

        return RegistrationResponseDTO.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .name(member.getName())
                .authorities(Stream.of(authority).map(auth -> auth.getAuthorityName().toString()).collect(Collectors.toSet()))
                .build();
    }

    @Builder
    public RegistrationResponseDTO(String username, String password, String name, Set<String> authorities) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.authorities = authorities;
    }
}
