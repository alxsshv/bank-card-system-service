package com.alxsshv.bank_card_system_service.service;

import com.alxsshv.bank_card_system_service.dto.request.MoneyTransferRequest;

public interface MoneyTransferService {
    void transferMoney(MoneyTransferRequest request, long userId);
}
