package com.dnlab.tacktogetherbackend.auth.dto;

import com.dnlab.tacktogetherbackend.auth.domain.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class MemberInfoResponseDTO implements Serializable {
    private String username;
    private String nickname;
    private String name;

    @Builder
    public MemberInfoResponseDTO(String username, String nickname, String name) {
        this.username = username;
        this.nickname = nickname;
        this.name = name;
    }

    public static MemberInfoResponseDTO of(Member member) {
        return MemberInfoResponseDTO.builder()
                .nickname(member.getNickname())
                .username(member.getUsername())
                .name(member.getName())
                .build();
    }
}
