package com.example.finly.finance.infraestructure.handler.exception;

import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;

public class UserNotExistsException extends ErrorCodeException{

    public UserNotExistsException(){
        super(ETypeErrorCodes.E4000003);
    }

    public UserNotExistsException(String message){
        super(ETypeErrorCodes.E4000003, message);
    }
}
