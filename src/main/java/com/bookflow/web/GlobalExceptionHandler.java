package com.bookflow.web;

import com.bookflow.dto.ErrorResponse;
import com.bookflow.exception.AlreadyReturnedException;
import com.bookflow.exception.BookNotFoundException;
import com.bookflow.exception.LoanNotFoundException;
import com.bookflow.exception.NoCopiesAvailableException;
import com.bookflow.exception.UnauthorizedLoanAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BookNotFoundException.class, LoanNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({NoCopiesAvailableException.class, AlreadyReturnedException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedLoanAccessException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(UnauthorizedLoanAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }
}
