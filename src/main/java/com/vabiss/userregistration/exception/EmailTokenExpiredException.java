package com.vabiss.userregistration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.IM_USED)
public class EmailTokenExpiredException extends RuntimeException {
    public EmailTokenExpiredException(String message) {
        super(String.format("%s token expired.", message));
    }
}
