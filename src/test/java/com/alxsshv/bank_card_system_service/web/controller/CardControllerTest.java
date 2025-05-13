package com.alxsshv.bank_card_system_service.web.controller;


import com.alxsshv.bank_card_system_service.configuration.AppConstants;
import com.alxsshv.bank_card_system_service.dto.request.CreateCardRequest;
import com.alxsshv.bank_card_system_service.exception.EntityNotFoundException;
import com.alxsshv.bank_card_system_service.model.Card;
import com.alxsshv.bank_card_system_service.model.CardStatus;
import com.alxsshv.bank_card_system_service.model.SystemRole;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.repository.CardRepository;
import com.alxsshv.bank_card_system_service.repository.UserRepository;
import com.alxsshv.bank_card_system_service.service.CardService;
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CardControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CardService cardService;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private final static PostgreSQLContainer<?> POSTGRES
            = new PostgreSQLContainer<>("postgres:17.5");
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MathContext mathContext = new MathContext(AppConstants.DECIMAL_PRECISION, RoundingMode.HALF_UP);

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

        Card card1 = Card.builder()
                .number(passwordEncoder.encode("1234123412341234"))
                .publicNumberPart("1234")
                .numberHash("1234123412341234".hashCode())
                .expireDate(Instant.now().plusSeconds(3600))
                .balanceInRub(new BigDecimal("100000.00", mathContext).setScale(2,RoundingMode.HALF_UP))
                .status(CardStatus.ACTIVE)
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .build();

        Card card2 = Card.builder()
                .number(passwordEncoder.encode("1111111111111111"))
                .publicNumberPart("1111")
                .numberHash("1111111111111111".hashCode())
                .expireDate(Instant.now().plusSeconds(3600))
                .balanceInRub(new BigDecimal("0.01", mathContext).setScale(2,RoundingMode.HALF_UP))
                .status(CardStatus.ACTIVE)
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .build();
Card card3 = Card.builder()
                .number(passwordEncoder.encode("2222111111111111"))
                .publicNumberPart("1111")
                .numberHash("2222111111111111".hashCode())
                .expireDate(Instant.now().minusSeconds(3600*24*7))
                .balanceInRub(new BigDecimal("0.01", mathContext).setScale(2,RoundingMode.HALF_UP))
                .status(CardStatus.EXPIRED)
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .build();

        Card card4 = Card.builder()
                .number(passwordEncoder.encode("4321432143214321"))
                .publicNumberPart("4321")
                .numberHash("4321432143214321".hashCode())
                .expireDate(Instant.now().plusSeconds(3600))
                .balanceInRub(new BigDecimal("100000.00", mathContext).setScale(2, RoundingMode.HALF_UP))
                .status(CardStatus.EXPIRED)
                .userId(userRepository.findByUsername("user2").orElseThrow().getId())
                .build();
        cardRepository.saveAll(List.of(card1, card2, card3, card4));
    }

    @AfterEach
    public void clearDatabase() {
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }


    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createCard when send valid request then get status created")
    public void testCreateCard_whenSendValidRequest_thenGetCreated() throws Exception {
        String cardNumber = "9999 9999 9999 9999";
        CreateCardRequest request = CreateCardRequest.builder()
                .number(cardNumber)
                .balance(new BigDecimal("0.00"))
                .status(CardStatus.ACTIVE.name())
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .build();

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createCard when send incorrect card number then get status bad request")
    public void testCreateCard_whenSendIncorrectCard_thenGetBadRequest() throws Exception {
        String cardNumber = "9999 9999 9999";
        CreateCardRequest request = CreateCardRequest.builder()
                .number(cardNumber)
                .balance(new BigDecimal("0.00"))
                .status(CardStatus.ACTIVE.name())
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .build();

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createCard when card number already exists then get status bad request")
    public void testCreateCard_whenCardNumberAlreadyExists_thenGetBadRequest() throws Exception {
        String cardNumber = "1111 1111 1111 1111";
        CreateCardRequest request = CreateCardRequest.builder()
                .number(cardNumber)
                .balance(new BigDecimal("0.00"))
                .status(CardStatus.ACTIVE.name())
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .build();

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createCard when user not found then get status bad request")
    public void testCreateCard_whenUserNotFound_thenGetBadRequest() throws Exception {
        String cardNumber = "9999 9999 9999 9999";
        CreateCardRequest request = CreateCardRequest.builder()
                .number(cardNumber)
                .balance(new BigDecimal("0.00"))
                .status(CardStatus.ACTIVE.name())
                .userId(userRepository.count() + 999L)
                .build();

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test createCard when status incorrect then get status bad request")
    public void testCreateCard_whenStatusIncorrect_thenGetBadRequest() throws Exception {
        String cardNumber = "9999 9999 9999 9999";
        CreateCardRequest request = CreateCardRequest.builder()
                .number(cardNumber)
                .balance(new BigDecimal("0.00"))
                .status("GOLD")
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .build();

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test activateCard when send valid request then get status ok")
    public void testActivateCard_whenSendValidRequest_thenGetStatusOk() throws Exception {
        long cardId = cardService.findByNumber("4321432143214321").getId();
        mockMvc.perform(patch("/api/v1/cards/activate/" + cardId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Card card = cardService.findByNumber("4321432143214321");
        Assertions.assertEquals(CardStatus.ACTIVE, card.getStatus());
    }


    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test blockCard when send valid request then get status ok")
    public void testBlockCard_whenSendValidRequest_thenGetStatusOk() throws Exception {
        long cardId = cardService.findByNumber("1111111111111111").getId();
        mockMvc.perform(patch("/api/v1/cards/block/" + cardId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Card card = cardService.findByNumber("1111111111111111");
        Assertions.assertEquals(CardStatus.BLOCKED, card.getStatus());
    }


    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test deleteCard when send valid request then get status ok")
    public void testDeleteCard_whenSendValidRequest_thenGetStatusOk() throws Exception {
        long cardId = cardService.findByNumber("1111111111111111").getId();
        mockMvc.perform(delete("/api/v1/cards/" + cardId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Assertions.assertThrows(
                EntityNotFoundException.class, () -> cardService.findByNumber("1111111111111111"));
    }

    @WithMockUser(value = "admin", username = "ADMIN", roles = "ADMIN")
    @Test
    @DisplayName("Test getAllCards when send request then get status ok")
    public void testGetALLCards_whenSendRequest_thenGetStatusOk() throws Exception {
        final int page = 0;
        final int size = 10;
        final String dir = "ASC";
        final Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.valueOf(dir), "number"));
        final int expectUsersCount = cardRepository.findAll(pageable).getContent().size();

        MvcResult result = mockMvc.perform(get("/api/v1/cards/all"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        final JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        final int actualUsersCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(expectUsersCount, actualUsersCount);
        Assertions.assertTrue(expectUsersCount > 0);
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test searchUserCardsByNumber when send valid request then get status ok")
    public void testSearchUserCardsByNumber_whenSendRequest_thenGetStatusOk() throws Exception {
        int expectedCount = 1;
        StringBuilder requestParams = new StringBuilder();
        requestParams.append("?")
                .append("page=").append("0").append("&")
                .append("size=").append("5").append("&")
                .append("dir=").append("ASC").append("&")
                .append("sort=").append("number").append("&")
                .append("search=").append("1111111111111111");

        MvcResult result = mockMvc.perform(get("/api/v1/cards/search/byNumber" + requestParams))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        final JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        final int actualCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(expectedCount, actualCount);
    }


    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test searchUserCardsByStatus when send valid request then get status ok")
    public void testSearchUserCardsByStatus_whenSendRequest_thenGetStatusOk() throws Exception {
        int expectedCount = 2;
        StringBuilder requestParams = new StringBuilder();
        requestParams.append("?")
                .append("page=").append("0").append("&")
                .append("size=").append("5").append("&")
                .append("dir=").append("ASC").append("&")
                .append("sort=").append("number").append("&")
                .append("search=").append("ACTIVE");

        MvcResult result = mockMvc.perform(get("/api/v1/cards/search/byStatus" + requestParams))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        final JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        final int actualCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test searchUserCardsByExpire when send valid request then get status ok")
    public void testSearchUserCardsByExpire_whenSendRequest_thenGetStatusOk() throws Exception {
        int expectedCount = 1;
        StringBuilder requestParams = new StringBuilder();
        requestParams.append("?")
                .append("page=").append("0").append("&")
                .append("size=").append("5").append("&")
                .append("dir=").append("ASC").append("&")
                .append("sort=").append("number").append("&")
                .append("start=").append("2025-05-01T10:15:30.00Z").append("&")
                .append("end=").append(Instant.now().minus(1, ChronoUnit.DAYS));

        MvcResult result = mockMvc.perform(get("/api/v1/cards/search/byExpire" + requestParams))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        final JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        final int actualCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(expectedCount, actualCount);
    }


    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test searchUserCardsByExpire when send incorrect start date then get status bad request")
    public void testSearchUserCardsByExpire_whenSendIncorrectStartDate_thenGetStatusBadRequest() throws Exception {
        int expectedCount = 1;
        StringBuilder requestParams = new StringBuilder();
        requestParams.append("?")
                .append("page=").append("0").append("&")
                .append("size=").append("5").append("&")
                .append("dir=").append("ASC").append("&")
                .append("sort=").append("number").append("&")
                .append("start=").append("2025-05-01").append("&")
                .append("end=").append(Instant.now());

        mockMvc.perform(get("/api/v1/cards/search/byExpire" + requestParams))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test blockCard when send valid request then get status ok")
    public void testUserBlockCard_whenSendValidRequest_thenGetStatusOk() throws Exception {
        long cardId = cardService.findByNumber("1234123412341234").getId();
        mockMvc.perform(patch("/api/v1/cards/set-block/" + cardId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Card card = cardService.findByNumber("1234123412341234");
        Assertions.assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test blockCard when user is not card owner then get status ok")
    public void testUserBlockCard_whenUserIsNotCardOwner_thenGetStatusOk() throws Exception {
        long cardId = cardService.findByNumber("4321432143214321").getId();
        mockMvc.perform(patch("/api/v1/cards/set-block/" + cardId))
                .andDo(print())
                .andExpect(status().isBadRequest());
        Card card = cardService.findByNumber("4321432143214321");
        Assertions.assertEquals(CardStatus.EXPIRED, card.getStatus());
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test getCardBalance when send valid request then get status ok")
    public void testGetCardBalance_whenSendValidRequest_thenGetStatusOk() throws Exception {
        Card card = cardService.findByNumber("1234123412341234");
        String expectedBalance = "100000.00";
        MvcResult result = mockMvc.perform(get("/api/v1/cards/" + card.getId()))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
        Assertions.assertEquals(expectedBalance, result.getResponse().getContentAsString());
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test getCardBalance when user is not card owner then get status ok")
    public void testGetCardBalance_whenUserIsNotCardOwner_thenGetStatusOk() throws Exception {
        Card card = cardService.findByNumber("4321432143214321");
        mockMvc.perform(get("/api/v1/cards/" + card.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


}
