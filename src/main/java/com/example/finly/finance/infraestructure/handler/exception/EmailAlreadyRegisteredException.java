package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class EmailAlreadyRegisteredException extends ErrorCodeException {

    public EmailAlreadyRegisteredException() {
        super(ETypeErrorCodes.E4000001);
    }

    public EmailAlreadyRegisteredException(String message) {
        super(ETypeErrorCodes.E4000001, message);
    }
}
