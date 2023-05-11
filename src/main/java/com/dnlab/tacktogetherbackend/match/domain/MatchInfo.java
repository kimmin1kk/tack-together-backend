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
@Table(name = "match_info")
public class MatchInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private Timestamp date;

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

    @Column(name = "total_fare")
    private Integer totalFare;

    @Column(name = "status", length = 45)
    @Enumerated(EnumType.STRING)
    private RidingStatus status;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "matchInfo")
    @ToString.Exclude
    private Set<MatchInfoMember> matchInfoMembers = new LinkedHashSet<>();

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = TimestampUtil.getCurrentTime();
        this.date = TimestampUtil.getCurrentTime();
    }

    @Builder
    public MatchInfo(
            Timestamp date,
            String origin,
                     String destination,
                     String waypoints,
                     int totalDistance,
                     Timestamp matchEndTime,
                     int totalFare,
                     RidingStatus status,
                     Set<MatchInfoMember> matchInfoMembers) {
        this.date = date;
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.totalDistance = totalDistance;
        this.matchEndTime = matchEndTime;
        this.totalFare = totalFare;
        this.status = status;
        this.matchInfoMembers = matchInfoMembers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MatchInfo that = (MatchInfo) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
