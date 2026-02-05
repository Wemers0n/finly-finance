package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class ErrorCodeException extends RuntimeException{

    protected ETypeErrorCodes errorCodes;

    protected ErrorCodeException(ETypeErrorCodes errorCodes){
        super(errorCodes.getMessage());
        this.errorCodes = errorCodes;
    }

    protected ErrorCodeException(ETypeErrorCodes errorCodes, Object... args){
        super(String.format(errorCodes.getMessage(), args));
        this.errorCodes = errorCodes;
    }

    public ETypeErrorCodes getErrorCodes(){
        return errorCodes;
    }
}
