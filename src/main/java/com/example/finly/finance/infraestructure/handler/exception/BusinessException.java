package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class BusinessException extends ErrorCodeException {
    public BusinessException(String message) {
        super(ETypeErrorCodes.E4000000, message);
    }
}
