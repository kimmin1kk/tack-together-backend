package com.dnlab.tacktogetherbackend.history.domain;


import com.dnlab.tacktogetherbackend.auth.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Duration;

@Entity
@Data
@NoArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String origin;

    @Column (nullable = false)
    private String destination;

    @Column (nullable = false)
    private Timestamp startTime;

    @Column (nullable = false)
    private Duration rideDuration;

    @Column (nullable = false)
    private Timestamp endTime;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_member_id")
    private Member passengerMember;

    @Column (nullable = false)
    private String savedCost;




}
