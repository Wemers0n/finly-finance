package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class TransactionDeniedException extends ErrorCodeException {

    public TransactionDeniedException(){
        super(ETypeErrorCodes.E4000005);
    }

    public TransactionDeniedException(String message) {
        super(ETypeErrorCodes.E4000005, message);
    }
}
