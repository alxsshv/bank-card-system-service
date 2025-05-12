package com.alxsshv.bank_card_system_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tokens", indexes = {@Index(columnList = "token")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

   @Column(name = "user_id")
    private long userId;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "expiration_date")
    private Instant expireDate;
}
