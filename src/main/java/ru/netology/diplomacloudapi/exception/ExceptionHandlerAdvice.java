package ru.netology.diplomacloudapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.netology.diplomacloudapi.entity.ErrorResponse;

import java.util.concurrent.atomic.AtomicInteger;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private AtomicInteger counter = new AtomicInteger(1);

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> errorInputData(ErrorInputData errorInputData) {
        return new ResponseEntity<>(new ErrorResponse(counter.getAndIncrement(), errorInputData.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> errorTransfer(InternalServerError errorTransfer) {
        return new ResponseEntity<>(new ErrorResponse(counter.getAndIncrement(), errorTransfer.getLocalizedMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> errorConfirmation(UnauthorizedError errorConfirmation) {
        return new ResponseEntity<>(new ErrorResponse(counter.getAndIncrement(), errorConfirmation.getLocalizedMessage()),
                HttpStatus.UNAUTHORIZED);
    }
}
