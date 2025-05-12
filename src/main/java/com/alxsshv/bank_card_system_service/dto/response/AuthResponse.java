package com.alxsshv.bank_card_system_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthResponse {
    private long id;
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private List<String> roles;
}
