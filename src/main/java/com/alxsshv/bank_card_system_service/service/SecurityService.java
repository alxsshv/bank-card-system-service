package com.alxsshv.bank_card_system_service.service;

import com.alxsshv.bank_card_system_service.dto.request.*;
import com.alxsshv.bank_card_system_service.dto.response.AuthResponse;
import com.alxsshv.bank_card_system_service.dto.response.RefreshTokenResponse;

public interface SecurityService {

    void registerUser(RegisterUserRequest registerUserRequest);

    void createUser(CreateUserRequest createUserRequest);

    void updateUser(UpdateUserRequest updateUserRequest);

    AuthResponse loginUser(LoginRequest loginRequest);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout();
}
