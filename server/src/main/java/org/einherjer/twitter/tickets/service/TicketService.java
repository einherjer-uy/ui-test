package org.einherjer.twitter.tickets.service;

import org.einherjer.twitter.tickets.model.Project;
import org.einherjer.twitter.tickets.model.Ticket;
import org.einherjer.twitter.tickets.repository.ProjectRepository;
import org.einherjer.twitter.tickets.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public void addComment(String projectPrefix, Integer ticketNumber, String comment) {
        Project project = projectRepository.findByPrefix(projectPrefix);
        Assert.notNull(project, "No project matches the specified prefix");
        Ticket ticket = ticketRepository.findByProjectAndNumber(project, ticketNumber);
        Assert.notNull(ticket, "No ticket matches the specified project and ticket number");
        ticket.addComment(comment);
    }

}
