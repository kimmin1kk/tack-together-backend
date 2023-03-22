package com.dnlab.tacktogetherbackend.user.domain;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<Authority> authorities = new HashSet<>();

    @Builder
    public Member(Long id, String username, String password, boolean enabled, Set<Authority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }
}
