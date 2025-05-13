package com.alxsshv.bank_card_system_service.configuration;

import com.alxsshv.bank_card_system_service.model.SystemRole;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "app.security.administrator")
@RequiredArgsConstructor
@Setter
@Slf4j
public class InitConfig {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder encoder;
    private String username;
    private String email;
    private String password;
    private boolean enabled;

    @PostConstruct
    public void initializeDefaultAdministrator() {
        if (username == null || email == null || password == null) {
            log.info("ВНИМАНИЕ! Свойства приложения не содержат сведения," +
                    "необходимые для регистрации администратора." +
                    "Аккаунт администратора не доступен для входа в систему");
        } else if (enabled && userRepository.findByUsername(username).isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .cards(new HashSet<>())
                    .password(encoder.encode(password))
                    .roles(Set.of(SystemRole.ROLE_ADMIN))
                    .build();
            userRepository.save(user);
            log.info("Зарегистрирован администратор системы");
        }
    }
}
