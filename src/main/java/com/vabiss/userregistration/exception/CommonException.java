package com.vabiss.userregistration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommonException extends RuntimeException {
    public CommonException(String entity) {
        super(entity);
    }
}
