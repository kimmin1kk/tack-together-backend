package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchInfoRepository extends JpaRepository<MatchInfo, Long> {

    @Query("SELECT mi " +
            "FROM MatchInfo mi " +
            "WHERE mi.id IN (" +
            "SELECT mim.matchInfo.id " +
            "FROM MatchInfoMember mim " +
            "WHERE mim.member.username = :username)")
    List<MatchInfo> findMatchInfosByMemberUsername(@Param("username") String username);

    MatchInfo findMatchInfoByMemberUsername(String username);
}
