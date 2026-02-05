package com.example.finly.finance.infraestructure.handler;

import com.example.finly.finance.infraestructure.handler.dto.ErrorResponse;
import com.example.finly.finance.infraestructure.handler.enums.ETypeErrorCodes;
import com.example.finly.finance.infraestructure.handler.exception.ErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException extends ResponseEntityExceptionHandler{

    @ExceptionHandler(ErrorCodeException.class)
    protected ResponseEntity<Object> handlerCoderError(ErrorCodeException e, WebRequest request){
        ErrorResponse response = new ErrorResponse();

        response.setError(e.getMessage());

        ETypeErrorCodes errorCodes = e.getErrorCodes();
        HttpStatus status = errorCodes.getStatus();
        response.setStatus(status.value());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.error("Error code: {}", e.getMessage());

        return handleExceptionInternal(e, response, headers, status, request);

    }
}
