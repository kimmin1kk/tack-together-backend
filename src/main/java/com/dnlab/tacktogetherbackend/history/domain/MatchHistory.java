package com.dnlab.tacktogetherbackend.history.domain;


import com.dnlab.tacktogetherbackend.auth.domain.Member;
import com.dnlab.tacktogetherbackend.match.domain.RidingStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "match_history")
public class MatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private Timestamp date;

    @Column(name = "origin")
    private String origin;

    @Column(name = "waypoints")
    private String waypoints;

    @Column(name = "destination")
    private String destination;

    @Column(name = "startTime")
    private Timestamp startTime;

    @Column(name = "endTime")
    private Timestamp endTime;

    @ManyToOne(fetch = FetchType.LAZY, mappedBy = "opponentMemberId")
    private Member opponentMember;

    @Column(name = "savedCost")
    private String savedCost;

    @Column(name = "totalCost")
    private String totalCost;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RidingStatus status;
}
