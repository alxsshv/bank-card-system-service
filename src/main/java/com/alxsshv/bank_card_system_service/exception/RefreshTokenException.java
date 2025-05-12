package com.alxsshv.bank_card_system_service.exception;

import java.text.MessageFormat;

public class RefreshTokenException extends RuntimeException {

  public RefreshTokenException(String token, String message) {
    super(MessageFormat
            .format("Ошибка обновления токена {0} : {1}", token, message));
  }

  public RefreshTokenException(String message) {
    super(message);
  }
}
