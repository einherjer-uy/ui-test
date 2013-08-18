package org.einherjer.twitter.tickets.service;

import org.einherjer.twitter.tickets.model.Attachment;
import org.einherjer.twitter.tickets.model.Project;
import org.einherjer.twitter.tickets.model.Ticket;
import org.einherjer.twitter.tickets.repository.ProjectRepository;
import org.einherjer.twitter.tickets.repository.TicketRepository;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class TicketService {

    @Autowired
    private LoginService loginService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public Iterable<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket find(String projectPrefix, Integer ticketNumber) {
        return this.find(projectPrefix, ticketNumber, false);
    }

    public Ticket find(String projectPrefix, Integer ticketNumber, boolean allowNull) {
        Project project = projectRepository.findByPrefix(projectPrefix);
        Assert.notNull(project, "No project matches the specified prefix");
        Ticket ticket = ticketRepository.findByProjectAndNumber(project, ticketNumber);
        if (!allowNull) {
            Assert.notNull(ticket, "No ticket matches the specified project and ticket number");
        }
        return ticket;
    }

    @Transactional
    public Ticket save(String projectPrefix, Integer ticketNumber, Ticket data) throws TicketNotFoundException {
        return internalSaveOrPatch(projectPrefix, ticketNumber, data, false);
    }

    @Transactional
    public Ticket patch(String projectPrefix, Integer ticketNumber, Ticket data) throws TicketNotFoundException {
        return internalSaveOrPatch(projectPrefix, ticketNumber, data, true);
    }

    private Ticket internalSaveOrPatch(String projectPrefix, Integer ticketNumber, Ticket data, boolean patch) throws TicketNotFoundException {
        if (!patch) {
            data.setAssignee(userRepository.findByUsername(data.getAssignee().getUsername()));
            Assert.notNull(data.getAssignee(), "No user matches the specified username");
        }
        Ticket ticket = this.find(projectPrefix, ticketNumber, true);
        if (ticket == null) {
            if (patch) {
                throw new TicketNotFoundException(projectPrefix, ticketNumber);
            }
            else{
                Project project = projectRepository.findByPrefix(projectPrefix);
                Assert.notNull(project, "No project matches the specified prefix");
                ticket = new Ticket(project, data, loginService.getLoggedUser());
                return ticketRepository.save(ticket);
            }
        }
        else {
            ticket.set(data);
            return ticket;
        }
    }

    @Transactional
    public void addComment(String projectPrefix, Integer ticketNumber, String comment) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        ticket.addComment(comment, loginService.getLoggedUser());
    }

    public Attachment getAttachment(String projectPrefix, Integer ticketNumber, Long attachmentId) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        return ticket.findAttachmentById(attachmentId);
    }

    @Transactional
    public void addAttachment(String projectPrefix, Integer ticketNumber, String filename, byte[] bytes) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        ticket.addAttachment(filename, bytes);
    }

    @Transactional
    public void deleteAttachment(String projectPrefix, Integer ticketNumber, Long attachmentId) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        ticket.removeAttachment(attachmentId);
    }
}
