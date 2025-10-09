package com.iara.config;

import com.iara.core.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalError> handle(HttpServletRequest httpServletRequest, Exception e) {
        GlobalError error = new GlobalError();

        if (AnnotationUtils.findAnnotation
                (e.getClass(), ResponseStatus.class) != null) {
            error.setStatus(Objects.requireNonNull(AnnotationUtils.findAnnotation
                    (e.getClass(), ResponseStatus.class)).code().value());
            error.setKey("GENERIC");
            error.setMessage("An unknow error occurred. If persist, contact your manager.");
        } else if (e instanceof BaseException) {
            error.setKey(((BaseException) e).getKey());
            error.setStatus(((BaseException) e).getStatus());
            error.setMessage(e.getMessage());
        } else {
            error.setKey("GENERIC");
            error.setStatus(500);
            error.setMessage("An unknow error occurred. If persist, contact your manager.");
        }

        error.setPath(httpServletRequest.getServletPath());

        log.error(e.getMessage(), e);

        return ResponseEntity.status(error.getStatus()).body(error);
    }
}
