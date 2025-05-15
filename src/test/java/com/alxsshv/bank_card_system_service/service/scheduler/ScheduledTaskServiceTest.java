package com.alxsshv.bank_card_system_service.service.scheduler;

import com.alxsshv.bank_card_system_service.model.*;
import com.alxsshv.bank_card_system_service.repository.CardRepository;
import com.alxsshv.bank_card_system_service.repository.RefreshTokenRepository;
import com.alxsshv.bank_card_system_service.repository.UserRepository;
import com.alxsshv.bank_card_system_service.service.CardService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ScheduledTaskServiceTest {
    @MockitoSpyBean
    private ScheduledTaskService scheduledTaskService;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CardService cardService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder encoder;

    private static final PostgreSQLContainer<?> POSTGRES
            = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("app.scheduler.expirationTokenDeleteInterval", () -> "7s");
        registry.add("app.scheduler.expirationCardInterval", () -> "5000");
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
    public void fillDatabase() {
        User user = User.builder()
                .username("User1")
                .email("user1@email.com")
                .password("UserPassword")
                .roles(Set.of(SystemRole.ROLE_USER))
                .cards(new HashSet<>())
                .build();
        userRepository.save(user);
    }

    @AfterEach
    public void clearDatabase() {
        refreshTokenRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("Test setExpireStatusForCards")
    public void testSetExpireStatusForCards() {
        final Duration waitValue = Duration.ofSeconds(10);
        final String cardNum = "2345234523452345";
        Card card = Card.builder()
                .number(encoder.encode(cardNum))
                .numberHash(cardNum.hashCode())
                .publicNumberPart("2345")
                .status(CardStatus.ACTIVE)
                .userId(userRepository.findByUsername("User1").orElseThrow().getId())
                .balanceInRub(new BigDecimal("0.00"))
                .expireDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
        cardRepository.save(card);

        await().atMost(waitValue)
                .untilAsserted(() -> {
                    verify(scheduledTaskService, atLeast(2)).setExpireStatusForCards();
                    Assertions.assertEquals(CardStatus.EXPIRED, cardService.findByNumber(cardNum).getStatus());
                });
    }

    @Test
    @DisplayName("Test deleteExpiredRefreshTokens")
    public void testDeleteExpiredRefreshTokens() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token("this-test-refresh-token")
                .userId(userRepository.findByUsername("User1").orElseThrow().getId())
                .expireDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
        refreshTokenRepository.save(refreshToken);
        long afterAddTokenCount = refreshTokenRepository.count();
        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    verify(scheduledTaskService, atLeast(2)).deleteExpiredRefreshTokens();
                    Assertions.assertEquals(afterAddTokenCount-1, refreshTokenRepository.count());
                });
    }
}
