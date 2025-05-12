package com.alxsshv.bank_card_system_service.service.implementation;

import com.alxsshv.bank_card_system_service.exception.EntityNotFoundException;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.repository.UserRepository;
import com.alxsshv.bank_card_system_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String EMAIL_TEMPLATE = "^\\w+([.-]?\\w+)@\\w+([.-]?\\w+)(.\\w)$";
    @Autowired
    private final UserRepository userRepository;

    @Override
    public User findById(long userId) throws EntityNotFoundException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("Пользователь c id = " + userId + " не найден");
        }
        return userOpt.get();
    }

    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> userOpt;
        if (usernameOrEmail.matches(EMAIL_TEMPLATE)) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        } else {
            userOpt = userRepository.findByUsername(usernameOrEmail);
        }
        return userOpt.orElseThrow(
                () -> new EntityNotFoundException("Пользователь " + usernameOrEmail + " не найден")
        );
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }


    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

}
