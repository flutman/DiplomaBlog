package com.example.diploma.errors;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    protected ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex, WebRequest req) {
        return handleExceptionInternal(
                ex,
                new ApiError(
                        false,
                        new PostErrorDto("Заголовок не установлен", "Текст публикации слишком короткий")
                ),
                HttpStatus.BAD_REQUEST,
                req
        );
    }

    @ExceptionHandler(value = {WrongPageException.class})
    protected ResponseEntity<String> handleNotFoundException(WrongPageException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    protected ResponseEntity<ApiError> handleExceptionInternal(
            Exception ex, ApiError body, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(body, null, status);
    }
}
