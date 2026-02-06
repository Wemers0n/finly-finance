package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class BankAccountNotFoundException extends ErrorCodeException {

    public BankAccountNotFoundException(){
        super(ETypeErrorCodes.E4000004);
    }

    public BankAccountNotFoundException(String message) {
        super(ETypeErrorCodes.E4000004, message);
    }
}
