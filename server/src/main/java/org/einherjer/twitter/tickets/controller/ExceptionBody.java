package org.einherjer.twitter.tickets.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ExceptionBody {

    private String message;
    private String stackTrace;

}
