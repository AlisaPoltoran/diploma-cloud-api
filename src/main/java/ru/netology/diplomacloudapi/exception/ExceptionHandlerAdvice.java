package ru.netology.diplomacloudapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.netology.diplomacloudapi.dto.ErrorResponse;

import java.util.concurrent.atomic.AtomicInteger;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private AtomicInteger counter = new AtomicInteger(1);

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> errorInputData(ErrorInputData errorInputData) {
        log.error(errorInputData.getMessage());
        return new ResponseEntity<>(new ErrorResponse(counter.getAndIncrement(), errorInputData.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> errorTransfer(InternalServerError errorTransfer) {
        log.error(errorTransfer.getMessage());
        return new ResponseEntity<>(new ErrorResponse(counter.getAndIncrement(), errorTransfer.getLocalizedMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> errorConfirmation(UnauthorizedError errorConfirmation) {
        log.error(errorConfirmation.getMessage());
        return new ResponseEntity<>(new ErrorResponse(counter.getAndIncrement(), errorConfirmation.getLocalizedMessage()),
                HttpStatus.UNAUTHORIZED);
    }
}
