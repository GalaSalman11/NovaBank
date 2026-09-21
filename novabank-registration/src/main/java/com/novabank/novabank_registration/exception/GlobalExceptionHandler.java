package com.novabank.novabank_registration.exception;

import com.novabank.novabank_registration.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception exception, WebRequest webRequest) {
        ErrorDto error = new ErrorDto(
                webRequest.getDescription(false) ,
                "INTERNAL_ERROR",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        List<FieldError> fieldErrorList =
                exception.getBindingResult().getFieldErrors();

        fieldErrorList.forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .badRequest()
                .body(errors);
    }
//VALIDATION_FAILED
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDto> handleNullPointerException(Exception exception, WebRequest webRequest) {
        ErrorDto error = new ErrorDto(
                webRequest.getDescription(false) ,
                "Cannot Accept Null Values",
                "Error Happen Duo TO : "+exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEmail(
            DuplicateEmailException exception,
            WebRequest webRequest) {

        ErrorDto error = new ErrorDto(
                webRequest.getDescription(false).replace("uri=", ""),
                "EMAIL_ALREADY_REGISTERED",
                "Email is already registered",
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(DuplicateMobileException.class)
    public ResponseEntity<ErrorDto> handleDuplicateMobile(
            DuplicateMobileException exception,
            WebRequest webRequest) {

        ErrorDto error = new ErrorDto(
                webRequest.getDescription(false).replace("uri=", ""),
                "MOBILE_ALREADY_REGISTERED",
                "Mobile number is already registered",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorDto> handlePasswordMismatch (
            PasswordMismatchException exception,
            WebRequest webRequest) {

        ErrorDto error = new ErrorDto(
                webRequest.getDescription(false).replace("uri=", ""),
                "PASSWORD_MISMATCH",
                "Password And Confirm Password Mismatch",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> Malformed_JSON  (
            HttpMessageNotReadableException exception,
            WebRequest webRequest) {

        ErrorDto error = new ErrorDto(
                webRequest.getDescription(false).replace("uri=", ""),
                "MALFORMED_REQUEST",
                "Json Request",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

}
