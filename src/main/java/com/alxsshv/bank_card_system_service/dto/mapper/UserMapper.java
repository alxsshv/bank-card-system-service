package com.alxsshv.bank_card_system_service.dto.mapper;

import com.alxsshv.bank_card_system_service.dto.response.UserDto;
import com.alxsshv.bank_card_system_service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Enum::name).toList())
                .build();
    }

    public Page<UserDto> toUserDtoPage(Page<User> users) {
        return users.map(this::toUserDto);
    }
}

