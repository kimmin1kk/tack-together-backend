package com.dnlab.tacktogetherbackend.user.domain;

import javax.validation.constraints.NotNull;

import com.dnlab.tacktogetherbackend.auth.domain.Authority;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private List<Authority> authorities = new ArrayList<>();

    @Builder
    public Member(Long id, String username, String password, boolean enabled, List<Authority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }
}
