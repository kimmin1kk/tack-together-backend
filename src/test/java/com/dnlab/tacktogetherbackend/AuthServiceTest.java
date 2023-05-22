package com.dnlab.tacktogetherbackend;

import com.dnlab.tacktogetherbackend.auth.common.JwtTokenProvider;
import com.dnlab.tacktogetherbackend.auth.common.Role;
import com.dnlab.tacktogetherbackend.auth.domain.Authority;
import com.dnlab.tacktogetherbackend.auth.domain.Member;
import com.dnlab.tacktogetherbackend.auth.dto.RefreshTokenRequestDTO;
import com.dnlab.tacktogetherbackend.auth.dto.LoginRequestDTO;
import com.dnlab.tacktogetherbackend.auth.repository.AuthorityRepository;
import com.dnlab.tacktogetherbackend.auth.service.AuthService;
import com.dnlab.tacktogetherbackend.auth.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = SpringBootTestConfiguration.class)
class AuthServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthService authService;

    private Member member;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    @Transactional
    public void setUp() {
        member = memberRepository.save(Member.builder()
                .username("testUser")
                .password("testPassword")
                .enabled(true)
                .build());
        authorityRepository.save(Authority.builder()
                .member(member)
                .authorityName(Role.ROLE_MEMBER)
                .build());

        accessToken = jwtTokenProvider.createAccessToken(new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword()));
        refreshToken = jwtTokenProvider.createRefreshToken();
    }

    @Test
    void testRegistration() throws Exception {
        log.info(member.toString());
        log.info(accessToken);
        log.info(refreshToken);

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .build();

        mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    void testRefreshToken() throws Exception {
        RefreshTokenRequestDTO refreshTokenRequestDTO = RefreshTokenRequestDTO.builder().refreshToken(refreshToken).build();

        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(refreshTokenRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void testGetUsernameFromAccessToken() {
        assertThat(jwtTokenProvider.getAuthentication(accessToken).getName(), is(member.getUsername()));
    }

    @AfterEach
    @Transactional
    public void tearDown() {
        authorityRepository.deleteByMember(member);
        memberRepository.delete(member);
    }
}
