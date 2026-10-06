package com.novabank.novabank_registration.exception;

public class DuplicateMobileException extends RuntimeException {
    public DuplicateMobileException(String message){
        super(message);
    }
}
