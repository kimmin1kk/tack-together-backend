package com.dnlab.tacktogetherbackend.match.config;

import com.dnlab.tacktogetherbackend.match.common.RangeKind;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "match.range")
@Getter
@RequiredArgsConstructor
public class MatchRangeProperties {
    private final Range origin;
    private final Range destination;

    public int convertRangeLevelToRange(short rangeLevel, RangeKind rangeKind) {
        switch (rangeKind) {
            case ORIGIN:
                return convertOriginRangeLevelToRange(rangeLevel);
            case DESTINATION:
                return convertDestinationRangeLevelToRange(rangeLevel);
            default:
                throw new IllegalArgumentException("올바른 범위레벨 종류를 입력해주세요");
        }
    }

    private int convertOriginRangeLevelToRange(short rangeLevel) {
        Range range = getOrigin();
        return getRangeByRangeLevel(rangeLevel, range);
    }

    private int convertDestinationRangeLevelToRange(short rangeLevel) {
        Range range = getDestination();
        return getRangeByRangeLevel(rangeLevel, range);
    }


    private int getRangeByRangeLevel(short rangeLevel, Range range) {
        switch (rangeLevel) {
            case 0:
                return range.getNarrow();
            case 1:
                return range.getNormal();
            case 2:
                return range.getWide();
            default:
                throw new IllegalArgumentException("올바른 레벨 단위를 입력해주세요.");
        }
    }

    @Data
    public static class Range {
        private int narrow;
        private int normal;
        private int wide;
    }
}
