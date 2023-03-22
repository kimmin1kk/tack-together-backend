package com.dnlab.tacktogetherbackend;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringJUnitConfig
@ComponentScan(basePackages = {"com.dnlab.tacktogetherbackend"})
@WebAppConfiguration
public class SpringBootTestConfiguration {
}
