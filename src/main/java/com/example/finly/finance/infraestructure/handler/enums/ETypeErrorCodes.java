package com.example.finly.finance.infraestructure.handler.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ETypeErrorCodes {
    E5000000(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error without mapped cause."),
    E4000001(HttpStatus.BAD_REQUEST, "An user with this e-mail already exists: %s"),
    E4000002(HttpStatus.BAD_REQUEST, "Bad request to: %s"),
    E4000003(HttpStatus.BAD_REQUEST, "This user not exists: %s");

    private final HttpStatus status;
    private final String message;

    ETypeErrorCodes(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }
}
