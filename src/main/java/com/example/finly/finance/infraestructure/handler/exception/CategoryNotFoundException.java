package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class CategoryNotFoundException extends ErrorCodeException {

    public CategoryNotFoundException() {
        super(ETypeErrorCodes.E4000006);
    }

    public CategoryNotFoundException(String message) {
        super(ETypeErrorCodes.E4000006, message);
    }
}
