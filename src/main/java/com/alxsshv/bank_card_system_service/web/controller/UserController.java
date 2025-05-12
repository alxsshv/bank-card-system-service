package com.alxsshv.bank_card_system_service.web.controller;

import com.alxsshv.bank_card_system_service.configuration.AppConstants;
import com.alxsshv.bank_card_system_service.dto.mapper.UserMapper;
import com.alxsshv.bank_card_system_service.dto.request.CreateUserRequest;
import com.alxsshv.bank_card_system_service.dto.request.UpdateUserRequest;
import com.alxsshv.bank_card_system_service.dto.response.SimpleResponse;
import com.alxsshv.bank_card_system_service.dto.response.UserDto;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.service.SecurityService;
import com.alxsshv.bank_card_system_service.service.UserService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleResponse> createUser(
            @Validated @RequestBody CreateUserRequest request) {
        securityService.createUser(request);
        final String message = "Добавлен пользователь " + request.getUsername();
        log.info(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse(message));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> findAllUsers(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR) String pageDir,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_PAGE_SORT_BY) String sortBy) {
        final Sort sort = Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), sortBy);
        final Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        final Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(userMapper.toUserDtoPage(users));
    }


    @PutMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleResponse> updateUser(@Validated @RequestBody UpdateUserRequest request) {
        securityService.updateUser(request);
        final String message = "Данные пользователя " + request.getUsername() + " успешно обновлены";
        log.info(message);
        return ResponseEntity.ok().body(new SimpleResponse(message));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleResponse> deleteUser(@PathVariable("id") long id) {
        userService.deleteUserById(id);
        final String message = "Данные пользователя с id = " + id + " успешно удалены";
        log.info(message);
        return ResponseEntity.ok().body(new SimpleResponse(message));
    }



}
