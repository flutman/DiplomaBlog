package com.example.diploma.exception;

import javassist.NotFoundException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    protected ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex, WebRequest req) {
        return handleExceptionInternal(
                ex,
                new ApiError(),
                HttpStatus.BAD_REQUEST,
                req
        );
    }

    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex, WebRequest req) {
        return handleExceptionInternal(
                ex,
                new ApiError(Map.of("post", "Пост не найден")),
                HttpStatus.BAD_REQUEST,
                req
        );
    }

    @ExceptionHandler(value = {UploadException.class, MaxUploadSizeExceededException.class, SizeLimitExceededException.class})
    protected ResponseEntity<ApiError> handleUploadException() {
        ApiError error = new ApiError();
        error.setErrors(Map.of("image", "Размер файла превышает допустимый размер"));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {WrongPageException.class})
    protected ResponseEntity<String> handleWrongPageException(WrongPageException ex) {
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
