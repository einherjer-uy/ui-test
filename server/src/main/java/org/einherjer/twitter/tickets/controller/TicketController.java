package org.einherjer.twitter.tickets.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.einherjer.twitter.tickets.model.Attachment;
import org.einherjer.twitter.tickets.model.Ticket;
import org.einherjer.twitter.tickets.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /**
     * Add Comment
     * 
     * Example request:
     * 
     * PUT /tickets/PR1-1/comment HTTP/1.1
     * Host: localhost:8080
     * Content-Type: application/json
     * {"comment":"comment"}
     */
    //Jackson can be used with "full data binding" (e.g. @RequestBody CommentJson), or "simple data binding" (e.g. @ResponseBody Map<String, Object>). 
    //      Both kinds of data binding can be used in either @ResponseBody or @RequestBody.
    @RequestMapping(value = "/tickets/{project}-{number}/comment", method = RequestMethod.PUT)
    public @ResponseBody Map<String, Object> addComment(
            @RequestBody CommentJson jsonBody,
            @PathVariable("project") String projectPrefix,
            @PathVariable("number") Integer ticketNumber) {
        ticketService.addComment(projectPrefix, ticketNumber, jsonBody.getComment());
        return new HashMap<String, Object>() {
            {
                put("message", "ok");
            }
        };
    }

    @Getter
    @Setter
    public static class CommentJson {
        private String comment;
    }

    /**
     * Get ticket
     */
    @RequestMapping(value = "/tickets/{project}-{number}", method = RequestMethod.GET)
    public @ResponseBody Ticket getTicket(@PathVariable("project") String projectPrefix, @PathVariable("number") Integer ticketNumber) {
        return ticketService.find(projectPrefix, ticketNumber);
    }

    /**
     * Add attachment
     * 
     * Example request (rest):
     * 
     * POST /someUrl
     * Content-Type: multipart/mixed
     * --edt7Tfrdusa7r3lNQc79vXuhIIMlatb7PQg7Vp
     * Content-Disposition: form-data; name="file-data"; filename="file.properties"
     * Content-Type: text/xml
     * Content-Transfer-Encoding: 8bit
     * ... File Data ...
     */
    @RequestMapping(value = "/tickets/{project}-{number}/attachment", method = RequestMethod.PUT)
    public @ResponseBody Map<String, Object> addAttachment(
            @PathVariable("project") String projectPrefix,
            @PathVariable("number") Integer ticketNumber,
            @RequestPart("file-data") MultipartFile file) throws IOException {
        ticketService.addAttachment(projectPrefix, ticketNumber, file.getOriginalFilename(), file.getBytes());
        return new HashMap<String, Object>() {
            {
                put("message", "ok");
            }
        };
    }
    
    /**
     * Delete attachment
     */
    @RequestMapping(value = "/tickets/{project}-{number}/attachment/{attachId}", method = RequestMethod.DELETE)
    public @ResponseBody Map<String, Object> deleteAttachment(
            @PathVariable("project") String projectPrefix,
            @PathVariable("number") Integer ticketNumber,
            @PathVariable("attachId") Long attachmentId) {
        ticketService.deleteAttachment(projectPrefix, ticketNumber, attachmentId);
        return new HashMap<String, Object>() {
            {
                put("message", "ok");
            }
        };
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
    
}
