package com.dnlab.tacktogetherbackend.match.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fare.minimum")
@Getter
@Setter
public class MinimumFareProperties {
    private int fare;
    private int distance;
}
