package com.alxsshv.bank_card_system_service.service.implementation;

import com.alxsshv.bank_card_system_service.dto.request.*;
import com.alxsshv.bank_card_system_service.dto.response.AuthResponse;
import com.alxsshv.bank_card_system_service.dto.response.RefreshTokenResponse;
import com.alxsshv.bank_card_system_service.exception.EntityNotFoundException;
import com.alxsshv.bank_card_system_service.exception.UserOperationException;
import com.alxsshv.bank_card_system_service.model.RefreshToken;
import com.alxsshv.bank_card_system_service.model.SystemRole;
import com.alxsshv.bank_card_system_service.model.User;
import com.alxsshv.bank_card_system_service.security.AppUserDetails;
import com.alxsshv.bank_card_system_service.security.jwt.JwtUtils;
import com.alxsshv.bank_card_system_service.service.RefreshTokenService;
import com.alxsshv.bank_card_system_service.service.SecurityService;
import com.alxsshv.bank_card_system_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    private final RefreshTokenService refreshTokenService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse loginUser(LoginRequest loginRequest) {
        final Authentication authentication =  getAuthentication(loginRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal();
        final List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(appUserDetails.getId());
        return AuthResponse.builder()
                .id(appUserDetails.getId())
                .username(appUserDetails.getUsername())
                .email(appUserDetails.getEmail())
                .roles(roles)
                .token(jwtUtils.generateJwtToken(appUserDetails.getUsername()))
                .refreshToken(refreshToken.getToken())
                .build();
    }

    private Authentication getAuthentication(LoginRequest loginRequest) {
        final User user = userService.findByUsernameOrEmail(loginRequest.getLogin());
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));
    }

    @Override
    public void registerUser(RegisterUserRequest createUserRequest) {
        final User user = User.builder()
                .username(createUserRequest.getUsername())
                .email(createUserRequest.getEmail())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .roles(Set.of(SystemRole.ROLE_USER))
                .build();
        userService.saveUser(user);
    }

    @Override
    public void createUser(CreateUserRequest request) {
        final User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .build();
        userService.saveUser(user);
    }

    @Override
    public void updateUser(UpdateUserRequest updateUserRequest) {
        final User user = userService.findByUsernameOrEmail(updateUserRequest.getUsername());
        if (!isThisUserEmailOrEmailNotExist(user, updateUserRequest.getEmail())) {
            throw new UserOperationException("Указанный адрес электронной почты " +
                    "уже используется другим пользователем");
        }
        user.setEmail(updateUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        user.setRoles(updateUserRequest.getRoles());
        userService.saveUser(user);
    }

    private boolean isThisUserEmailOrEmailNotExist(User user, String email) {
        User emailOwner;
        try {
            emailOwner = userService.findByUsernameOrEmail(email);
        } catch (EntityNotFoundException ex) {
            return true;
        }
        return emailOwner.getUsername().equals(user.getUsername());
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        final RefreshToken oldRefreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.checkRefreshToken(oldRefreshToken);
        final User tokenOwner = userService.findById(oldRefreshToken.getUserId());
        final String accessToken = jwtUtils.generateJwtToken(tokenOwner.getUsername());
        final String refreshToken = refreshTokenService.createRefreshToken(tokenOwner.getId()).getToken();
        return new RefreshTokenResponse(accessToken,  refreshToken);
    }

    @Override
    @Transactional
    public void logout() {
        final UserDetails currentPrincipal = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        final long userId = userService.findByUsernameOrEmail(currentPrincipal.getUsername()).getId();
        refreshTokenService.deleteByUserId(userId);
    }

}
