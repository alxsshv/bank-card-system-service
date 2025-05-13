package com.alxsshv.bank_card_system_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "cards")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "number", unique = true, nullable = false)
    private String number;

    @Column(name = "number_hash", nullable = false)
    private int numberHash;

    @Column(name = "public_number", nullable = false)
    private String publicNumberPart;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "expiration_date")
    private Instant expireDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "balance", precision = 12, scale = 2)
    private BigDecimal balanceInRub;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id && numberHash == card.numberHash && userId == card.userId && Objects.equals(number, card.number) && Objects.equals(publicNumberPart, card.publicNumberPart) && Objects.equals(expireDate, card.expireDate) && status == card.status && Objects.equals(balanceInRub, card.balanceInRub);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, numberHash, publicNumberPart, userId, expireDate, status, balanceInRub);
    }
}
