package com.dnlab.tacktogetherbackend;

import com.dnlab.tacktogetherbackend.auth.service.AuthService;
import com.dnlab.tacktogetherbackend.user.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = SpringBootTestConfiguration.class)
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testRegistration() {

    }
}
