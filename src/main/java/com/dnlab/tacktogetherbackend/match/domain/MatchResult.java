package com.dnlab.tacktogetherbackend.match.domain;

import com.dnlab.tacktogetherbackend.global.util.TimestampUtil;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "match_result")
public class MatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "waypoints", nullable = false)
    private String waypoints;

    @Column(name = "total_fare", nullable = false)
    private int totalFare;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matchResult")
    private List<MatchResultMember> matchResultMembers;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = TimestampUtil.getCurrentTime();
    }
}
