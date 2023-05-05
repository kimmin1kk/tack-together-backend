package com.dnlab.tacktogetherbackend.match.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fare.minimum")
@Getter
@RequiredArgsConstructor
public class MinimumFareProperties {
    private final int fare;
    private final int distance;
}
