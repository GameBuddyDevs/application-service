package com.back2261.applicationservice.exception;

import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<DefaultMessageResponse> handleException(Exception ex, HttpServletRequest request) {
        DefaultMessageResponse response = new DefaultMessageResponse();
        Status status = new Status();
        status.setMessage(ex.getMessage());
        status.setSuccess(false);
        status.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        response.setStatus(status);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<DefaultMessageResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        try {
            List<String> errors = new ArrayList<>();
            for (DefaultMessageSourceResolvable error : ex.getBindingResult().getAllErrors()) {
                errors.add(error.getDefaultMessage());
            }

            DefaultMessageResponse response = new DefaultMessageResponse();
            Status status = new Status();
            status.setMessage(errors.toString());
            status.setSuccess(false);
            status.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setStatus(status);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler(BusinessException.class)
    public final ResponseEntity<DefaultMessageResponse> handleKFBusinessException(
            BusinessException ex, HttpServletRequest request) {
        try {
            DefaultMessageResponse response = new DefaultMessageResponse();
            Status status = new Status();
            status.setMessage(ex.getTransactionCode().getCode());
            status.setSuccess(false);
            status.setCode(Integer.toString(ex.getTransactionCode().getId()));
            response.setStatus(status);
            return new ResponseEntity<>(response, ex.getTransactionCode().getHttpStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
