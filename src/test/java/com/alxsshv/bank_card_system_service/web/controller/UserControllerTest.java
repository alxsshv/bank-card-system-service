package com.alxsshv.bank_card_system_service.web.controller;

import com.alxsshv.bank_card_system_service.dto.request.CreateUserRequest;
import com.alxsshv.bank_card_system_service.dto.request.UpdateUserRequest;
import com.alxsshv.bank_card_system_service.model.SystemRole;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private final static PostgreSQLContainer<?> POSTGRES
            = new PostgreSQLContainer<>("postgres:17.5");
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
                .username("alreadyExistUser")
                .email("alreadyExistUser@user.com")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(SystemRole.ROLE_ADMIN))
                .build();
        User user2 = User.builder()
                .username("userFromDb")
                .email("userFromDB@user.com")
                .password(passwordEncoder.encode("passwordUserFromDb"))
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    public void clearDatabase() {
        userRepository.deleteAll();
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when send valid CreateUserRequest then get status created (201)")
    public void testCreateUser_whenSendValidCreateUserRequest_thenGetStatusCreated() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("user@mail.com")
                .password("12345")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

                mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                        .andExpect(status().isCreated());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when username length is less than valid min then get status bad request (400)")
    public void testCreateUser_whenUsernameLengthLessThanMin_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("u")
                .email("user@mail.com")
                .password("12345")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when username length exceeds the allowed maximum then get status bad request (400)")
    public void testCreateUser_whenUsernameLengthExceedsMax_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("0b88ltvQgq9TPRXjxhVj6QUGqfWjpssBTs3Lf8ghwXvi6sE4Ui0")
                .email("user@mail.com")
                .password("12345")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when users username already exists  then get status bad request (400)")
    public void testCreateUser_whenUsernameAlreadyExist_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("alreadyExistUser")
                .email("user@mail.com")
                .password("12345")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when username is null then get status bad request (400)")
    public void testCreateUser_whenUsernameIsNull_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("user@mail.com")
                .password("12345")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when password is null then get status bad request (400)")
    public void testCreateUser_whenPasswordIsNull_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("user@mail.com")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when password length is less than allowed min then get status bad request (400)")
    public void testCreateUser_whenPasswordLessThanMin_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("user@mail.com")
                .password("1")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when password length exceeds the allowed maximum then get status bad request (400)")
    public void testCreateUser_whenPasswordExceedMax_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("user@mail.com")
                .password("cbynaeD7qKAqwPVdxd0dkmINuDJBALfuc9XqUhw0eYdeLs0XcY2")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when email is null then get status bad request (400)")
    public void testCreateUser_whenEmailIsNull_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .password("12345")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when email has incorrect value then get status bad request (400)")
    public void testCreateUser_whenEmailIncorrect_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("qeqwdada.rus")
                .password("12345")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when users email already exists then get status bad request (400)")
    public void testCreateUser_whenEmailAlreadyExist_thenGetStatusBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("alreadyExistUser@user.com")
                .password("12345")
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createUser when roles is null then get status created (201) with default user role ")
    public void testCreateUser_whenRolesIsNull_thenGetStatusCreated() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("user@user.com")
                .password("12345")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test updateUser when send valid request then get status ok (200)")
    public void testUpdateUser_whenSendValidRequest_thenGetStatusOk() throws Exception {
        Set<SystemRole> roleSet = Set.of(SystemRole.ROLE_USER, SystemRole.ROLE_ADMIN);
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username("alreadyExistUser")
                .email("user1@user.com")
                .password("54321")
                .roles(roleSet)
                .build();

        mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Optional<User> userOpt = userRepository.findByEmail("user1@user.com");
        Assertions.assertTrue(userOpt.isPresent());
        Assertions.assertEquals(roleSet.size(), userOpt.get().getRoles().size());
        Assertions.assertTrue(passwordEncoder.matches("54321", userOpt.get().getPassword()));
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test updateUser when new email already exists request then get status bad request")
    public void testUpdateUser_whenNewEmailUserAlreadyExists_thenGetStatusBadRequest() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username("alreadyExistUser")
                .email("userFromDB@user.com")
                .password("54321")
                .roles(Set.of(SystemRole.ROLE_USER, SystemRole.ROLE_ADMIN))
                .build();

        mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test deleteUser when send valid id then get status ok")
    public void testDeleteUser_whenSendValidId_thenGetStatusOk() throws Exception {
        long userId = userRepository.findByUsername("alreadyExistUser").orElseThrow().getId();
        mockMvc.perform(delete("/api/v1/users/" + userId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Optional<User> userOpt = userRepository.findByUsername("alreadyExistUser");
        Assertions.assertTrue(userOpt.isEmpty());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test getAllUsers when send request then get status ok")
    public void testGetALLUser_whenSendRequest_thenGetStatusOk() throws Exception {
        final int page = 0;
        final int size = 10;
        final String dir = "ASC";
        final Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.valueOf(dir), "Username"));
        final int expectUsersCount = userRepository.findAll(pageable).getContent().size();

        MvcResult result = mockMvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        final JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        final int actualUsersCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(expectUsersCount, actualUsersCount);
    }



}
