package org.einherjer.twitter.tickets.controller;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.einherjer.twitter.tickets.model.Attachment;
import org.einherjer.twitter.tickets.model.Comment;
import org.einherjer.twitter.tickets.model.Ticket;
import org.einherjer.twitter.tickets.service.TicketNotFoundException;
import org.einherjer.twitter.tickets.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;


    @RequestMapping(value = "/tickets", method = RequestMethod.GET)
    public @ResponseBody Iterable<Ticket> getTickets() {
        return ticketService.findAll();
    }
    
    /**
     * Get ticket
     */
    @RequestMapping(value = "/tickets/{project}-{number}", method = RequestMethod.GET)
    public @ResponseBody Ticket getTicket(@PathVariable("project") String projectPrefix, @PathVariable("number") Integer ticketNumber) {
        return ticketService.find(projectPrefix, ticketNumber);
    }

    /**
     * Add ticket
     * 
     * Example request:
     * POST /api/tickets HTTP/1.1
     * Host: localhost:8080
     * Content-Type: application/json
     * { "project":{"prefix":"PR1"}, "title":"t", "description":"d", "status":"OPEN", "assignee":{"username":"user@twitter.com"} }
     */
    @RequestMapping(value = "/tickets", method = RequestMethod.POST)
    public @ResponseBody TicketIdInfo /*ResponseEntity<String>*/ postTicket(@RequestBody Ticket jsonBody) throws TicketNotFoundException {
        this.validateTicketDTO(jsonBody);
        Assert.notNull(jsonBody.getProject(), "Project cannot be null");
        Assert.notNull(jsonBody.getProject().getPrefix(), "Project cannot be null");
        Ticket ticket = ticketService.save(jsonBody.getProject().getPrefix(), -1, jsonBody);
        /*return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);*///usually a POST will return HttpStatus.CREATED and an empty body, but in this case we include the ticket id that the server generated in the response
        return new TicketIdInfo(ticket.getTicketId()); 
    }
    
    @AllArgsConstructor
    public static class TicketIdInfo {
        @JsonProperty("number")
        private @Getter @Setter String ticketId;
    }
    
    /**
     * Update ticket
     * 
     * Example request:
     * PUT /api/tickets/PR1-1 HTTP/1.1
     * Host: localhost:8080
     * Content-Type: application/json
     * { "title":"t", "description":"d", "status":"OPEN", "assignee":{"username":"user@twitter.com"} }
     */
    @RequestMapping(value = "/tickets/{project}-{number}", method = RequestMethod.PUT)
    public ResponseEntity<String> putTicket(
            @RequestBody Ticket jsonBody,
            @PathVariable("project") String projectPrefix, 
            @PathVariable("number") Integer ticketNumber) throws TicketNotFoundException {
        this.validateTicketDTO(jsonBody);
        ticketService.save(projectPrefix, ticketNumber, jsonBody);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
    
    private void validateTicketDTO(Ticket jsonBody){
        Assert.notNull(jsonBody.getTitle(), "Title cannot be null");
        Assert.notNull(jsonBody.getDescription(), "Description cannot be null");
        Assert.notNull(jsonBody.getStatus(), "Status cannot be null");
        Assert.notNull(jsonBody.getAssignee(), "Assignee cannot be null");
        Assert.notNull(jsonBody.getAssignee().getUsername(), "Assignee cannot be null");
    }
    
    /**
     * Patch ticket
     * 
     * Example request:
     * PATCH /api/tickets/PR1-1 HTTP/1.1
     * Host: localhost:8080
     * Content-Type: application/json
     * { "status":"CANCELLED" }
     */
    @RequestMapping(value = "/tickets/{project}-{number}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchTicket(
            @RequestBody Ticket jsonBody,
            @PathVariable("project") String projectPrefix, 
            @PathVariable("number") Integer ticketNumber) throws TicketNotFoundException {
        ticketService.patch(projectPrefix, ticketNumber, jsonBody);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
    
    @ExceptionHandler(TicketNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionBody handleTicketNotFoundException(TicketNotFoundException e) {
        return new ExceptionBody(e.getMessage(), ExceptionUtils.getStackTrace(e));
    }

    /**
     * Add Comment
     * 
     * Example request:
     * POST /api/tickets/PR1-1/comment HTTP/1.1
     * Host: localhost:8080
     * Content-Type: application/json
     * {"text":"comment text"}
     */
    @RequestMapping(value = "/tickets/{project}-{number}/comment", method = RequestMethod.POST)
    public ResponseEntity<String> addComment(
            @RequestBody Comment jsonBody,
            @PathVariable("project") String projectPrefix,
            @PathVariable("number") Integer ticketNumber) {
        ticketService.addComment(projectPrefix, ticketNumber, jsonBody.getText());
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }
    
    /**
     * Get attachment
     */
    @RequestMapping(value = "/tickets/{project}-{number}/attachment/{attachId}", method = RequestMethod.GET)
    public @ResponseBody Attachment getAttachment(
            @PathVariable("project") String projectPrefix,
            @PathVariable("number") Integer ticketNumber,
            @PathVariable("attachId") Long attachmentId) {
        return ticketService.getAttachment(projectPrefix, ticketNumber, attachmentId);
    }
    
    /**
     * Add attachment
     * 
     * Example request:
     * POST /api/tickets/PR1-1/attachment HTTP/1.1
     * Content-Type: multipart/mixed
     * --edt7Tfrdusa7r3lNQc79vXuhIIMlatb7PQg7Vp
     * Content-Disposition: form-data; name="file-data"; filename="file.properties"
     * Content-Type: text/xml
     * Content-Transfer-Encoding: 8bit
     * ... File Data ...
     */
    @RequestMapping(value = "/tickets/{project}-{number}/attachment", method = RequestMethod.POST)
    public ResponseEntity<String> addAttachment(
            @PathVariable("project") String projectPrefix,
            @PathVariable("number") Integer ticketNumber,
            @RequestPart("file-data") MultipartFile file) throws IOException {
        ticketService.addAttachment(projectPrefix, ticketNumber, file.getOriginalFilename(), file.getBytes());
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }
    
    /**
     * Delete attachment
     */
    @RequestMapping(value = "/tickets/{project}-{number}/attachment/{attachId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAttachment(
            @PathVariable("project") String projectPrefix,
            @PathVariable("number") Integer ticketNumber,
            @PathVariable("attachId") Long attachmentId) {
        ticketService.deleteAttachment(projectPrefix, ticketNumber, attachmentId);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

}
