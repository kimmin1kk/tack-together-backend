//package com.dnlab.tacktogetherbackend.match.domain;
//
//import com.dnlab.tacktogetherbackend.auth.domain.Member;
//import lombok.Data;
//
//import javax.persistence.*;
//
//@Entity
//@Data
//@Table(name = "matching_result")
//public class MatchingResult {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;
//
//    private boolean matched;
//}
