package com.dnlab.tacktogetherbackend;

import com.dnlab.tacktogetherbackend.user.common.MemberRegistrationDTO;
import com.dnlab.tacktogetherbackend.user.common.MemberResponseDTO;
import com.dnlab.tacktogetherbackend.user.repository.MemberRepository;
import com.dnlab.tacktogetherbackend.user.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = SpringBootTestConfiguration.class)
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void registrationTest() {
        MemberRegistrationDTO dto = new MemberRegistrationDTO();
        dto.setUsername("test1");
        dto.setPassword("test1");
        MemberResponseDTO memberResponseDTO = memberService.registerMember(dto);

        log.info("registered member : " + memberRepository.findMemberByUsername(memberResponseDTO.getUsername()).orElseThrow());
        assertThat(memberResponseDTO.getUsername(), is(dto.getUsername()));
    }
}
