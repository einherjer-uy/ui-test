package org.einherjer.twitter.tickets.controller;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
@Slf4j
public class Advice {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ExceptionBody handleIllegalArgumentException(IllegalArgumentException e) {
        return new ExceptionBody(e.getMessage(), ExceptionUtils.getStackTrace(e));
    }

    //hack to be able to debug bad request exceptions that doesn't show up in the log
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        log.error("bad request caught by org.einherjer.twitter.tickets.controller.Advice", e);
    }

}
