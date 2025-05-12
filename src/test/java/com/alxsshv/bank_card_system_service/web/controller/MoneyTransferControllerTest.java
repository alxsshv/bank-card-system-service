package com.alxsshv.bank_card_system_service.web.controller;

import com.alxsshv.bank_card_system_service.configuration.AppConstants;
import com.alxsshv.bank_card_system_service.dto.request.MoneyTransferRequest;
import com.alxsshv.bank_card_system_service.model.Card;
import com.alxsshv.bank_card_system_service.model.CardStatus;
import com.alxsshv.bank_card_system_service.model.SystemRole;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.repository.CardRepository;
import com.alxsshv.bank_card_system_service.repository.UserRepository;
import com.alxsshv.bank_card_system_service.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoneyTransferControllerTest {
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
                .number(passwordEncoder.encode("2222111111112222"))
                .publicNumberPart("2222")
                .numberHash("2222111111112222".hashCode())
                .expireDate(Instant.now().plusSeconds(3600))
                .balanceInRub(new BigDecimal("8000.00", mathContext).setScale(2,RoundingMode.HALF_UP))
                .status(CardStatus.EXPIRED)
                .userId(userRepository.findByUsername("user1").orElseThrow().getId())
                .build();

        Card card4 = Card.builder()
                .number(passwordEncoder.encode("4321432143214321"))
                .publicNumberPart("4321")
                .numberHash("4321432143214321".hashCode())
                .expireDate(Instant.now().plusSeconds(3600))
                .balanceInRub(new BigDecimal("100000.00", mathContext).setScale(2, RoundingMode.HALF_UP))
                .status(CardStatus.ACTIVE)
                .userId(userRepository.findByUsername("user2").orElseThrow().getId())
                .build();
        cardRepository.saveAll(List.of(card1, card2, card3, card4));
    }

    @AfterEach
    public void clearDatabase() {
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test transfer when send valid request then get status ok")
    public void testTransfer_whenSendValidRequest_thenGetStatusOK() throws Exception {
        final Card fromCard = cardService.findByNumber("1234123412341234");
        final Card toCard = cardService.findByNumber("1111111111111111");
        MoneyTransferRequest request = MoneyTransferRequest.builder()
                .fromCardId(fromCard.getId())
                .toCardId(toCard.getId())
                .transferSumInRub("7000.00")
                .build();

        mockMvc.perform(post("/api/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        BigDecimal actualBalance = cardService.findByNumber("1111111111111111").getBalanceInRub();
        Assertions.assertEquals(0, new BigDecimal("7000.01").compareTo(actualBalance));
    }

    @WithMockUser(value = "user2", username = "user2", password = "passwordUserFromDb")
    @Test
    @DisplayName("Test transfer when user is not card owner request then get status bad request")
    public void testTransfer_whenUserIsNotCardOwner_thenGetStatusBadRequest() throws Exception {
        final Card fromCard = cardService.findByNumber("1234123412341234");
        final Card toCard = cardService.findByNumber("1111111111111111");
        MoneyTransferRequest request = MoneyTransferRequest.builder()
                .fromCardId(fromCard.getId())
                .toCardId(toCard.getId())
                .transferSumInRub("7000.00")
                .build();

        mockMvc.perform(post("/api/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        BigDecimal actualBalance = cardService.findByNumber("1111111111111111").getBalanceInRub();
        Assertions.assertEquals(0, new BigDecimal("0.01").compareTo(actualBalance));
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test transfer when card balance is low for transfer then create status ok")
    public void testTransfer_whenCardBalanceIsLow_thenCreateStatusOk() throws Exception {
        final Card fromCard = cardService.findByNumber("1111111111111111");
        final Card toCard = cardService.findByNumber("1234123412341234");
        MoneyTransferRequest request = MoneyTransferRequest.builder()
                .fromCardId(fromCard.getId())
                .toCardId(toCard.getId())
                .transferSumInRub("7000.00")
                .build();

        mockMvc.perform(post("/api/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        BigDecimal actualBalance = cardService.findByNumber("1234123412341234").getBalanceInRub();
        Assertions.assertEquals(0, new BigDecimal("100000.00").compareTo(actualBalance));
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test transfer when from card is expired then create status ok")
    public void testTransfer_whenFromCardIsExpired_thenCreateStatusOk() throws Exception {
        final Card fromCard = cardService.findByNumber("2222111111112222");
        final Card toCard = cardService.findByNumber("1111111111111111");
        MoneyTransferRequest request = MoneyTransferRequest.builder()
                .fromCardId(fromCard.getId())
                .toCardId(toCard.getId())
                .transferSumInRub("7000.00")
                .build();

        mockMvc.perform(post("/api/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        BigDecimal actualBalance = cardService.findByNumber("1111111111111111").getBalanceInRub();
        Assertions.assertEquals(0, new BigDecimal("0.01").compareTo(actualBalance));
    }

    @WithMockUser(value = "user1", username = "user1", password = "pass1")
    @Test
    @DisplayName("Test transfer when to card is expired then create status ok")
    public void testTransfer_whenToCardIsExpired_thenCreateStatusOk() throws Exception {
        final Card fromCard = cardService.findByNumber("1234123412341234");
        final Card toCard = cardService.findByNumber("2222111111112222");
        MoneyTransferRequest request = MoneyTransferRequest.builder()
                .fromCardId(fromCard.getId())
                .toCardId(toCard.getId())
                .transferSumInRub("7000.00")
                .build();

        mockMvc.perform(post("/api/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        BigDecimal actualBalance = cardService.findByNumber("1234123412341234").getBalanceInRub();
        Assertions.assertEquals(0, new BigDecimal("100000.00").compareTo(actualBalance));
    }
}
