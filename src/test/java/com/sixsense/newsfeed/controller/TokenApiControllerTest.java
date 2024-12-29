package com.sixsense.newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixsense.newsfeed.config.jwt.JwtProperties;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.CreateAccessTokenRequestDto;
import com.sixsense.newsfeed.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;

import static com.sixsense.newsfeed.constant.Token.AUTHORIZATION_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext content;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    private TokenProvider tokenProvider;

    @BeforeEach
    void setMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(content).build();
        userRepository.deleteAll();
    }

    @DisplayName("AccessToken 재발급 성공")
    @Test
    void createNewAccessTokenTest() throws Exception {
        // then
        String url = "/api/token";
        User testUser = userRepository.save(
                User.builder()
                        .name("홍길동")
                        .email("user@gmail.com")
                        .address("제주특별자치도 ~~")
                        .age(40)
                        .password("12345")
                        .build()
        );
        String accessToken = tokenProvider.generateToken(testUser, Duration.ofHours(1));
        String refreshToken = tokenProvider.generateToken(testUser, Duration.ofDays(7));
        String requestBody = objectMapper.writeValueAsString(new CreateAccessTokenRequestDto(refreshToken));

        // when
        ResultActions result = mockMvc.perform(post(url)
                .header(AUTHORIZATION_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isCreated());
    }
}