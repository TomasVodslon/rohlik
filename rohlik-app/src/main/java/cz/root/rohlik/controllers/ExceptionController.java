package cz.root.rohlik.controllers;

import cz.root.rohlik.transfer.ErrorMessageTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleInvalidInputException(RuntimeException ex) {
        ErrorMessageTO errorMessageTO = new ErrorMessageTO();
        errorMessageTO.setError(ex.getMessage());
        return new ResponseEntity<>(errorMessageTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
