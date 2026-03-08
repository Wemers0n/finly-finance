package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class InvoiceNotFoundException extends ErrorCodeException {

    public InvoiceNotFoundException() {
        super(ETypeErrorCodes.E4000009);
    }

    public InvoiceNotFoundException(String message) {
        super(ETypeErrorCodes.E4000009, message);
    }
}