package com.alxsshv.bank_card_system_service.web.controller;

import com.alxsshv.bank_card_system_service.dto.request.LoginRequest;
import com.alxsshv.bank_card_system_service.dto.request.RefreshTokenRequest;
import com.alxsshv.bank_card_system_service.dto.request.RegisterUserRequest;
import com.alxsshv.bank_card_system_service.model.RefreshToken;
import com.alxsshv.bank_card_system_service.model.SystemRole;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.repository.RefreshTokenRepository;
import com.alxsshv.bank_card_system_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Instant;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private final static PostgreSQLContainer<?> POSTGRES
            = new PostgreSQLContainer<>("postgres:17.5");
    @DynamicPropertySource
    public static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeAll
    public static void startDatabase() {
        POSTGRES.start();
    }

    @AfterAll
    public static void stopDatabase() {
        POSTGRES.stop();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeEach
    public void fillDatabase() {
        User user1 = User.builder()
                .username("user1")
                .email("user1@user.com")
                .password(passwordEncoder.encode("pass1"))
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();
        User user2 = User.builder()
                .username("user2")
                .email("user2@user.com")
                .password(passwordEncoder.encode("passwordUserFromDb"))
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

    }

    @AfterEach
    public void clearDatabase() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("test loginUser when send login request by username request then get status ok")
    public  void testLoginUser_whenSendValidRequestByUsername_thenGetStatusOk() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .login("user1")
                .password("pass1")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("test loginUser when send login request by email request then get status ok")
    public  void testLoginUser_whenSendValidRequestByEmail_thenGetStatusOk() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .login("user1@user.com")
                .password("pass1")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("test loginUser when user not found then get status not found")
    public  void testLoginUser_whenUserNotFound_thenGetStatusNotFound() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("pass1")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("test loginUser when incorrect password then get status bad credentials (401)")
    public  void testLoginUser_whenIncorrectPassword_thenGetStatusBadCredentials() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .login("user1")
                .password("incorrectPassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @DisplayName("test registerUser when send valid request then get status created")
    public void testRegisterUser_whenSendValidRequest_thenGetStatusCreated() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("user9")
                .email("user9@mail.com")
                .password("12345")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("test registerUser when username already exists then get status bad request")
    public void testRegisterUser_whenUsernameAlreadyExists_thenGetStatusBadRequest() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("user1")
                .email("user9@mail.com")
                .password("12345")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test registerUser when email is incorrect then get status bad request")
    public void testRegisterUser_whenEmailIncorrect_thenGetStatusBadRequest() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("user9")
                .email("user9mail.com")
                .password("12345")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test registerUser when email already exists then get status bad request")
    public void testRegisterUser_whenEmailAlreadyExists_thenGetStatusBadRequest() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("user9")
                .email("user2@user.com")
                .password("12345")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test registerUser when password length less than allowed min " +
            "already exists then get status bad request")
    public void testRegisterUser_whenPasswordLessThanMin_thenGetStatusBadRequest() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("user9")
                .email("user9@user.com")
                .password("12")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test refreshToken when send valid request then get status ok")
    public void testRefreshToken_whenSendValidRequest_thenGetStatusOK() throws Exception {
        String token = "090d3ad9-be58-4e6d-bf0f-29bbd4524c8b";
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .token(token)
                .expireDate(Instant.now().plusSeconds(3600))
                .build();
        refreshTokenRepository.save(refreshToken);
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(token);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Test refreshToken when token not found then get status forbidden")
    public void testRefreshToken_whenTokenNotFound_thenGetStatusForbidden() throws Exception {
        String token = "090d3ad9-be58-4e6d-bf0f-bbbbd4524c8b";
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .token(token)
                .expireDate(Instant.now().plusSeconds(3600))
                .build();
        refreshTokenRepository.save(refreshToken);
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("11234567-be58-4e6d-bf0f-bbbbd4524789");

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test logout when send valid request then get status ok")
    public void testLogout_whenSendRequest_thenGetStatusOk() throws Exception {
        String token = "090d3ad9-be58-4e6d-bf0f-29bbd4524c8b";
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .token(token)
                .expireDate(Instant.now().plusSeconds(3600))
                .build();
        refreshTokenRepository.save(refreshToken);
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(token);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Assertions.assertTrue(refreshTokenRepository.findByToken(token).isEmpty());
    }



}
