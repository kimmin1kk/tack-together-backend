package com.dnlab.tacktogetherbackend.user.domain;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @Size(max = 45)
    @Column(name = "authority", nullable = false, length = 45)
    private String authority;

    @Builder
    public Authority(Long id, Member member, String authority) {
        this.id = id;
        this.member = member;
        this.authority = authority;
    }
}
