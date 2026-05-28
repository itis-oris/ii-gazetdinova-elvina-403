package ru.isgaij.smartcloset.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice(annotations = Controller.class)
public class MvcExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MvcExceptionHandler.class);

    @ExceptionHandler({ResourceNotFoundException.class, NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception e, Model model) {
        log.warn("Resource not found: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAny(Exception e, Model model) {
        log.error("Internal server error: {}", e.getMessage(), e);
        model.addAttribute("message", e.getMessage());
        return "error/500";
    }
}
