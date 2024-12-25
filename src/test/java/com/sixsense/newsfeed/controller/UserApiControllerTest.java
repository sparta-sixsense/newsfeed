package com.sixsense.newsfeed.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixsense.newsfeed.config.PasswordEncoder;
import com.sixsense.newsfeed.config.jwt.TokenProvider;
import com.sixsense.newsfeed.domain.Status;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.SignUpRequestDto;
import com.sixsense.newsfeed.dto.UpdateUserRequestDto;
import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.UserInactiveOrDeletedException;
import com.sixsense.newsfeed.repository.UserRepository;
import com.sixsense.newsfeed.service.UserService;
import org.assertj.core.api.Assertions;
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
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext context;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("CreateUser: 입력값이 조건에 안 맞으면 회원가입 실패")
    @Test
    void createUserValidationTest() throws Exception {
        // given
        String url = "/signup";
        // 회원가입시 각종 필드 조건 맞아야 함
        SignUpRequestDto requestDto = new SignUpRequestDto("test@gmail.com", "123", "강성욱", "123", 10);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        result.andExpect(status().isBadRequest());
    }

    @DisplayName("CreateUser: 회원가입 성공")
    @Test
    void createUserTest() throws Exception {
        // given
        String url = "/signup";
        SignUpRequestDto requestDto = new SignUpRequestDto("test@gmail.com", "Ksu1234!!!", "강성욱", "경기도 ~~", 10);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        result.andExpect(status().isCreated());

        User findUser = userRepository.findAll().get(0);
        assertThat(findUser.getName()).isEqualTo(requestDto.name());
        assertThat(findUser.getEmail()).isEqualTo(requestDto.email());
        assertThat(findUser.getAddress()).isEqualTo(requestDto.address());
        assertThat(findUser.getAge()).isEqualTo(requestDto.age());
        assertThat(passwordEncoder.matches(requestDto.password(), findUser.getPassword())).isTrue();
    }

    @DisplayName("회원 수정 성공")
    @Test
    void updateUser() throws Exception {
        // given
        String url = "/api/users/{id}";
        User savedUser = makeTestUser();
        String accessToken = tokenProvider.generateToken(savedUser, Duration.ofDays(1));
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto("Kkh1234!3", "강성욱", "사랑시 고백구 행복동", 100);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        ResultActions result = mockMvc.perform(put(url, savedUser.getId())
                .header(AUTHORIZATION_HEADER, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value(requestDto.name()))
                .andExpect(jsonPath("$.address").value(requestDto.address()))
                .andExpect(jsonPath("$.age").value(requestDto.age()));
    }

    @DisplayName("delete user test")
    @Test
    void deleteUserTest() throws Exception {
        // given
        String url = "/api/users/{id}";
        User savedUser = makeTestUser();
        String accessToken = tokenProvider.generateToken(savedUser, Duration.ofDays(1));

        // when
        ResultActions result = mockMvc.perform(delete(url, savedUser.getId())
                .header(AUTHORIZATION_HEADER, accessToken)
        );

        // then
        result.andExpect(status().isOk());

        User deletedUser = userRepository.findById(savedUser.getId()).get();
        assertThat(deletedUser.getStatus()).isEqualTo(Status.DELETED);
        assertThatThrownBy(() -> {
            userService.delete(savedUser.getId(), accessToken);
        })
                .isInstanceOf(UserInactiveOrDeletedException.class)
                .hasMessage(ErrorCode.USER_INACTIVE_OR_DELETED.getMessage());
    }

    @DisplayName("Get user")
    @Test
    void getSpecificUserTest() throws Exception {
        // given
        String url = "/api/users/{id}";
        User savedUser = makeTestUser();

        // when
        ResultActions result = mockMvc.perform(get(url, savedUser.getId())
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value(savedUser.getName()))
                .andExpect(jsonPath("$.age").value(savedUser.getAge()))
                .andExpect(jsonPath("$.address").value(savedUser.getAddress()))
                .andExpect(jsonPath("$.createdAt").value(savedUser.getCreatedAt().toString()));
    }

    User makeTestUser() {
        return userRepository.save(User.builder()
                .name("하하하")
                .email("test@gmail.com")
                .age(10)
                .address("뉴욕특파원시 맨해튼동")
                .password("Ksu12345!")
                .build());
    }
}