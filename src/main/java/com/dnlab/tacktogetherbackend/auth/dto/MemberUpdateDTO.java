package com.dnlab.tacktogetherbackend.auth.dto;

import com.dnlab.tacktogetherbackend.auth.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDTO {
    private String nickname;
    private String password;
    private String name;

    public static MemberUpdateDTO of(Member member) {
        return new MemberUpdateDTO(member.getNickname(),
                member.getPassword(),
                member.getName());
    }
}
