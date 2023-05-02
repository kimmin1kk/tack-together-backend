package com.dnlab.tacktogetherbackend.match.domain;

import com.dnlab.tacktogetherbackend.global.util.TimestampUtil;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @Column(name = "total_distance", nullable = false)
    private int totalDistance;

    @Column(name = "match_end_time")
    private Timestamp matchEndTime;

    @Column(name = "total_fare", nullable = false)
    private int totalFare;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matchResult")
    @ToString.Exclude
    private Set<MatchResultMember> matchResultMembers = new LinkedHashSet<>();

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = TimestampUtil.getCurrentTime();
    }

    @Builder

    public MatchResult(Long id,
                       String origin,
                       String destination,
                       String waypoints,
                       int totalDistance,
                       Timestamp matchEndTime,
                       int totalFare,
                       Set<MatchResultMember> matchResultMembers,
                       Timestamp createTime) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.totalDistance = totalDistance;
        this.matchEndTime = matchEndTime;
        this.totalFare = totalFare;
        this.matchResultMembers = matchResultMembers;
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MatchResult that = (MatchResult) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
