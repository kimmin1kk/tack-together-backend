package com.dnlab.tacktogetherbackend.match.domain;

import com.dnlab.tacktogetherbackend.auth.domain.Member;
import com.dnlab.tacktogetherbackend.global.util.TimestampUtil;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "match_result_member")
@NoArgsConstructor
public class MatchResultMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_result_id")
    private MatchResult matchResult;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "payment_fare", nullable = false)
    private int paymentAmount;

    @Column(name = "distance", nullable = false)
    private int distance;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = TimestampUtil.getCurrentTime();
    }

    @Builder
    public MatchResultMember(Long id, MatchResult matchResult, Member member, String destination, int paymentAmount, int distance, Timestamp createTime) {
        this.id = id;
        this.matchResult = matchResult;
        this.member = member;
        this.destination = destination;
        this.paymentAmount = paymentAmount;
        this.distance = distance;
        this.createTime = createTime;
    }
}
