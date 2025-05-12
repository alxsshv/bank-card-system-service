package com.alxsshv.bank_card_system_service.service;


import com.alxsshv.bank_card_system_service.exception.EntityNotFoundException;
import com.alxsshv.bank_card_system_service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User findById(long userId) throws EntityNotFoundException;
    User findByUsernameOrEmail(String usernameOrEmail);
    Page<User> findAll(Pageable pageable);
    void saveUser(User user);
    void deleteUserById(long userId);

}
