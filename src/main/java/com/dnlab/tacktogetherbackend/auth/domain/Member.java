package com.dnlab.tacktogetherbackend.auth.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "member",
        indexes = {@Index(name = "index_username", columnList = "username", unique = true)})
@Getter
@Setter
@ToString
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

    @Column(name = "create_time", nullable = false)
    @CreationTimestamp
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false)
    @UpdateTimestamp
    private Timestamp updateTime;

    @Builder
    @SuppressWarnings("squid:S107")
    public Member(Long id, String username, String password, String name, String nickname, boolean enabled, List<Authority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Member member = (Member) o;
        return getId() != null && Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
