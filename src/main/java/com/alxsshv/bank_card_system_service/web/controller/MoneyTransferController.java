package com.alxsshv.bank_card_system_service.web.controller;

import com.alxsshv.bank_card_system_service.dto.request.MoneyTransferRequest;
import com.alxsshv.bank_card_system_service.dto.response.SimpleResponse;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.service.MoneyTransferService;
import com.alxsshv.bank_card_system_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfer")
@Slf4j
public class MoneyTransferController {
    @Autowired
    private final MoneyTransferService moneyTransferService;
    @Autowired
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SimpleResponse> transferMoney(
            @Validated @RequestBody MoneyTransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsernameOrEmail(userDetails.getUsername());
        moneyTransferService.transferMoney(request, user.getId());
        String message = "Перевод денежных средств выполнен успешно";
        log.info(message);
        return ResponseEntity.ok(new SimpleResponse(message));
    }
}
