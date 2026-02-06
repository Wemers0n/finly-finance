package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class CreditCardNotFoundException extends ErrorCodeException {

    public CreditCardNotFoundException(){
        super(ETypeErrorCodes.E4000008);
    }

    public CreditCardNotFoundException(String message) {
        super(ETypeErrorCodes.E4000008, message);
    }
}
