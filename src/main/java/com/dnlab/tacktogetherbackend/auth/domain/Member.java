package com.dnlab.tacktogetherbackend.auth.domain;


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

    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Authority> authorities = new ArrayList<>();

    @Column(name = "refresh_token")
    private String refreshToken;

    @Builder
    public Member(Long id, String username, String password, String name, boolean enabled, List<Authority> authorities, String refreshToken) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.enabled = enabled;
        this.authorities = authorities;
        this.refreshToken = refreshToken;
    }
}
