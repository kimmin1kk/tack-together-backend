package com.dnlab.tacktogetherbackend.match.domain;

import com.dnlab.tacktogetherbackend.auth.domain.Member;
import com.dnlab.tacktogetherbackend.global.util.TimestampUtil;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "match_info_member")
public class MatchInfoMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_info_id")
    @ToString.Exclude
    private MatchInfo matchInfo;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "payment_fare")
    private int paymentAmount;

    @Column(name = "distance", nullable = false)
    private int distance;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "drop_off_time")
    private Timestamp dropOffTime;

    @PrePersist
    public void prePersist() {
        this.createTime = TimestampUtil.getCurrentTime();
    }

    @Builder
    public MatchInfoMember(Long id, MatchInfo matchInfo, Member member, String destination, int paymentAmount, int distance, Timestamp createTime, Timestamp dropOffTime) {
        this.id = id;
        this.matchInfo = matchInfo;
        this.member = member;
        this.destination = destination;
        this.paymentAmount = paymentAmount;
        this.distance = distance;
        this.createTime = createTime;
        this.dropOffTime = dropOffTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MatchInfoMember that = (MatchInfoMember) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
