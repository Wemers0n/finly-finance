package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class CategoryAlreadyExistsException extends ErrorCodeException {

    public CategoryAlreadyExistsException() {
        super(ETypeErrorCodes.E4000007);
    }

    public CategoryAlreadyExistsException(String message) {
        super(ETypeErrorCodes.E4000007, message);
    }
}
