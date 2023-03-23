package com.dnlab.tacktogetherbackend.auth.domain;

import com.dnlab.tacktogetherbackend.auth.common.Role;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity // 엔티티 선언
@Table(name = "authority") // 테이블 매핑
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Id 값의 생성 전략
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @ToString.Exclude
    private Member member;

    @Column(name = "authority_name", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private Role authorityName;

    @Builder
    public Authority(Long id, Member member, Role authorityName) {
        this.id = id;
        this.member = member;
        this.authorityName = authorityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Authority authority = (Authority) o;
        return id != null && Objects.equals(id, authority.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
