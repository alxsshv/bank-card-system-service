package com.alxsshv.bank_card_system_service.service.scheduler;

import com.alxsshv.bank_card_system_service.service.CardService;
import com.alxsshv.bank_card_system_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {
    private final RefreshTokenService refreshTokenService;
    private final CardService cardService;

    @Scheduled(fixedDelayString = "${app.scheduler.expirationCardInterval}")
    public void setExpireStatusForCards() {
        cardService.setExpireStatus();
        log.info("Планировщик задач: Выполнена задача изменения статуса просроченных карт");
    }

    @Scheduled(fixedDelayString = "${app.scheduler.expirationTokenDeleteInterval}")
    public void deleteExpiredRefreshTokens() {
        refreshTokenService.deleteExpiredTokens();
        log.info("Планировщик задач: Выполнено удаление просроченных токенов");
    }
}
