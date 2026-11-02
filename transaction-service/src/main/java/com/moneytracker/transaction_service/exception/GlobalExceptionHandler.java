package com.moneytracker.transaction_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleArgumentNotFound(MethodArgumentNotValidException excpetion){
        Map<String, List<String>> errors = new HashMap<>();

        for(FieldError error : excpetion.getBindingResult().getFieldErrors()){
            String field = error.getField();

            if(!errors.containsKey(field)){
                //se non contiene la chiave crea una lista
                errors.put(field, new ArrayList<>());
            }
            errors.get(field).add(error.getDefaultMessage());
        }
        ApiError apiError = new ApiError(
                LocalDateTime.now(ZoneId.systemDefault()),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(apiError);
    }
}
