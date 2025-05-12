package com.alxsshv.bank_card_system_service.web.controller;

import com.alxsshv.bank_card_system_service.configuration.AppConstants;
import com.alxsshv.bank_card_system_service.dto.request.CreateCardRequest;
import com.alxsshv.bank_card_system_service.dto.response.CardDto;
import com.alxsshv.bank_card_system_service.dto.response.SimpleResponse;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.service.CardService;
import com.alxsshv.bank_card_system_service.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
@Slf4j
public class CardController {
    @Autowired
    private final CardService cardService;
    @Autowired
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleResponse> createCard(
            @Validated @RequestBody CreateCardRequest createCardRequest) {
        cardService.create(createCardRequest);
        String message = "Новая банковская карта успешно добавлена";
        log.info(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse(message));
    }

    @PatchMapping("/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleResponse> activateCard(@PathVariable("id") long id) {
        cardService.activateById(id);
        String message = "Карта успешно активирована, id карты = " + id;
        log.info(message);
        return ResponseEntity.ok(new SimpleResponse(message));
    }

    @PatchMapping("/block/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleResponse> blockCard(@PathVariable("id") long id) {
        cardService.blockById(id);
        String message = "Карта заблокирована администратором, id карты = " + id;
        log.info(message);
        return ResponseEntity.ok(new SimpleResponse(message));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleResponse> deleteCard(@PathVariable("id") long id) {
        cardService.deleteById(id);
        String message = "Карта успешно удалена, id = " + id;
        log.info(message);
        return ResponseEntity.ok(new SimpleResponse(message));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDto>> getAllCards(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR) String pageDir,
            @RequestParam(value = "sort", defaultValue = AppConstants.DEFAULT_PAGE_SORT_BY) String sortBy) {
        final Sort sort = Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), sortBy);
        final Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        return ResponseEntity.ok(cardService.findAll(pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<CardDto>> getAllUserCards(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR) String pageDir,
            @RequestParam(value = "sort", defaultValue = AppConstants.DEFAULT_PAGE_SORT_BY) String sortBy,
            @AuthenticationPrincipal UserDetails userDetails) {
        final Sort sort = Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), sortBy);
        final Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        final User user = userService.findByUsernameOrEmail(userDetails.getUsername());
        return ResponseEntity.ok(cardService.findAllByUserId(user.getId(), pageable));
    }

    @GetMapping("/search/byNumber")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<CardDto>> searchUserCardsByNumber(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR) String pageDir,
            @RequestParam(value = "sort", defaultValue = AppConstants.DEFAULT_PAGE_SORT_BY) String sortBy,
            @RequestParam(value = "search", defaultValue = "")
            @NotBlank(message = "Поисковая строка пуста. " +
                    "Пожалуйста введите номер карты для поиска в формате: 1111 1111 1111 1111")
            @Pattern(regexp = "^([0-9]{4}\\s?){4}$", message = "Неверный формат номера карты. " +
                    "Пожалуйста введите номер карты для поиска в формате: 1111 1111 1111 1111") String searchNumber,
            @AuthenticationPrincipal UserDetails userDetails) {
        final Sort sort = Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), sortBy);
        final Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        final User user = userService.findByUsernameOrEmail(userDetails.getUsername());
        return ResponseEntity.ok(cardService.findAllByNumberAndUserId(searchNumber, user.getId(), pageable));
    }

    @GetMapping("/search/byStatus")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<CardDto>> searchUserCardsByStatus(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR) String pageDir,
            @RequestParam(value = "sort", defaultValue = AppConstants.DEFAULT_PAGE_SORT_BY) String sortBy,
            @RequestParam(value = "search", defaultValue = "") String searchStatus,
            @AuthenticationPrincipal UserDetails userDetails) {
        final Sort sort = Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), sortBy);
        final Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        final User user = userService.findByUsernameOrEmail(userDetails.getUsername());
        return ResponseEntity.ok(cardService.findAllByStatusAndUserId(searchStatus, user.getId(), pageable));
    }

    @GetMapping("/search/byExpire")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<CardDto>> searchUserCardsByExpireDate(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR) String pageDir,
            @RequestParam(value = "sort", defaultValue = AppConstants.DEFAULT_PAGE_SORT_BY) String sortBy,
            @RequestParam(value = "start") String startDate,
            @RequestParam(value = "end") String endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        final Sort sort = Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), sortBy);
        final Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        final User user = userService.findByUsernameOrEmail(userDetails.getUsername());
        return ResponseEntity.ok(cardService.findAllByExpireDateAndUserId(startDate, endDate, user.getId(), pageable));
    }


    @PatchMapping("/set-block/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SimpleResponse> userBlockCard(@PathVariable("id") long id,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        final User user = userService.findByUsernameOrEmail(userDetails.getUsername());
        cardService.blockByIdAndUserId(id, user.getId());
        String message = "Карта заблокирована по требованию пользователя, id карты = " + id;
        log.info(message);
        return ResponseEntity.ok(new SimpleResponse(message));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable("id") long id,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        final User user = userService.findByUsernameOrEmail(userDetails.getUsername());
        BigDecimal balanceInRub = cardService.getCardBalanceById(id, user.getId());
        return ResponseEntity.ok(balanceInRub);
    }

}
