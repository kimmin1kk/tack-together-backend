package com.dnlab.tacktogetherbackend.auth.domain;


import com.dnlab.tacktogetherbackend.global.util.TimestampUtil;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
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


    @Column(name = "username", nullable = false, length = 45, unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "nickname", nullable = false, length = 16)
    private String nickname;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private List<Authority> authorities = new ArrayList<>();

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Builder
    @SuppressWarnings("squid:S107")
    public Member(Long id, String username, String password, String name,String nickname, boolean enabled, List<Authority> authorities, String refreshToken) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.enabled = enabled;
        this.authorities = authorities;
        this.refreshToken = refreshToken;
    }

    @PrePersist
    public void prePersist() {
        Timestamp currentTime = TimestampUtil.getCurrentTime();
        this.createTime = currentTime;
        this.updateTime = currentTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = TimestampUtil.getCurrentTime();
    }
}
