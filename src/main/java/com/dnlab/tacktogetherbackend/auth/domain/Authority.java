package com.dnlab.tacktogetherbackend.auth.domain;

import com.dnlab.tacktogetherbackend.auth.common.MemberAuthority;
import com.dnlab.tacktogetherbackend.user.domain.Member;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "authority")
@Data
@RequiredArgsConstructor
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    @Column(name = "authority_name", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private MemberAuthority authorityName;

    @Builder
    public Authority(Long id, Member member, MemberAuthority authorityName) {
        this.id = id;
        this.member = member;
        this.authorityName = authorityName;
    }
}
