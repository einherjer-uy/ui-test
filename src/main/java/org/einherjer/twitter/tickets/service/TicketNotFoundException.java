package org.einherjer.twitter.tickets.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public TicketNotFoundException(String projectPrefix, Integer ticketNumber) {
        super("Ticket " + projectPrefix + "-" + ticketNumber + " doesn't exist");
    }

}
