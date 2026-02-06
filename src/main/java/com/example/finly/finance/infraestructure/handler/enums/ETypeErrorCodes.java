package com.example.finly.finance.infraestructure.handler.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ETypeErrorCodes {
    E5000000(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error without mapped cause."),
    E4000001(HttpStatus.BAD_REQUEST, "An user with this e-mail already exists: %s"),
    E4000002(HttpStatus.BAD_REQUEST, "Bad request to: %s"),
    E4000003(HttpStatus.BAD_REQUEST, "This user not exists: %s"),
    E4000004(HttpStatus.BAD_REQUEST, "This account does note exists: %s"),
    E4000005(HttpStatus.BAD_REQUEST, "Transaction denied: %s"),
    E4000006(HttpStatus.BAD_REQUEST, "This category does not exists."),
    E4000007(HttpStatus.BAD_REQUEST, "This category already exists."),
    E4000008(HttpStatus.BAD_REQUEST, "This credCard does not exists.");


    private final HttpStatus status;
    private final String message;

    ETypeErrorCodes(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }
}
