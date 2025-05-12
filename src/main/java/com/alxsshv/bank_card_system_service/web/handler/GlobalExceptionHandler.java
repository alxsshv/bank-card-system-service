package com.alxsshv.bank_card_system_service.web.handler;

import com.alxsshv.bank_card_system_service.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResponseBody> handleRefreshTokenException(RefreshTokenException ex,
                                                                         WebRequest webRequest) {
        log.error(ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), webRequest);
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<ErrorResponseBody> handleAlreadyExistException(AlreadyExistsException ex,
                                                                         WebRequest webRequest) {
        String errorMessage = "Попытка создания объекта, который уже существует: " + ex.getMessage();
        log.error(errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, webRequest);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> handleEntityNotFoundException(EntityNotFoundException ex,
                                                                           WebRequest webRequest) {
        String errorMessage = "Объект не найден: " + ex.getMessage();
        log.error(errorMessage);
        return buildResponse(HttpStatus.NOT_FOUND, errorMessage, webRequest);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseBody> handleConstraintViolationException(ConstraintViolationException ex,
                                                                           WebRequest webRequest) {
        String errorMessage = "Ошибка валидации данных: " + ex.getMessage();
        log.error(errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, webRequest);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                WebRequest webRequest) {
        String errorMessage = "Ошибка в параметрах запроса:  " + ex.getMessage();
        log.error(errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, webRequest);
    }

    @ExceptionHandler(value = MoneyTransferException.class)
    public ResponseEntity<ErrorResponseBody> handleMoneyTransferException(MoneyTransferException ex,
                                                                                   WebRequest webRequest) {
        String errorMessage = "Ошибка выполнения денежного перевода:  " + ex.getMessage();
        log.error(errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, webRequest);
    }

    @ExceptionHandler(value = CardOperationException.class)
    public ResponseEntity<ErrorResponseBody> handleCardOperationException(CardOperationException ex,
                                                                          WebRequest webRequest) {
        String errorMessage = "Ошибка выполнения операции с банковской картой:  " + ex.getMessage();
        log.error(errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, webRequest);
    }

    @ExceptionHandler(value = UserOperationException.class)
    public ResponseEntity<ErrorResponseBody> handleUserOperationException(UserOperationException ex,
                                                                          WebRequest webRequest) {
        String errorMessage = "Ошибка в запросе на выполнение операции c данными пользователя:  " + ex.getMessage();
        log.error(errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, webRequest);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(HttpStatus httpStatus,
                                                            String message,
                                                            WebRequest webRequest) {
        final ErrorResponseBody responseBody = ErrorResponseBody.builder()
                .message(message)
                .description(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(httpStatus).body(responseBody);
    }
}
