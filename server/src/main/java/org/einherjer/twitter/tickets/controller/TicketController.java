package org.einherjer.twitter.tickets.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.einherjer.twitter.tickets.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Slf4j
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /*
     * Example request:
     * 
     * PUT /tickets/PR1-1/comment HTTP/1.1
     * Host: localhost:8080
     * Content-Type: application/json
     * Cache-Control: no-cache
     * {"comment":"comment"}
     */
    @RequestMapping(value = "/tickets/{project}-{number}/comment", method = RequestMethod.PUT)
    public void addComment(@RequestBody CommentJson jsonBody, @PathVariable("project") String projectPrefix, @PathVariable("number") Integer ticketNumber) {
        ticketService.addComment(projectPrefix, ticketNumber, jsonBody.getComment());

        log.debug("after ticketService.addComment");
    }

    @Getter
    @Setter
    public static class CommentJson {
        private String comment;
    }

}
