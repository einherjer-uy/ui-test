package org.einherjer.twitter.tickets.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.einherjer.twitter.tickets.model.Attachment;
import org.einherjer.twitter.tickets.model.Project;
import org.einherjer.twitter.tickets.model.Session;
import org.einherjer.twitter.tickets.model.Ticket;
import org.einherjer.twitter.tickets.model.Ticket.TicketPriority;
import org.einherjer.twitter.tickets.model.Ticket.TicketStatus;
import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.repository.ProjectRepository;
import org.einherjer.twitter.tickets.repository.SessionRepository;
import org.einherjer.twitter.tickets.repository.TicketRepository;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.einherjer.twitter.tickets.websocket.NotificationService;
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
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private NotificationService notificationService;

    public Iterable<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Iterable<Ticket> findAllForRole() {
        User loggedUser = loginService.getLoggedUser();
        Iterable<Ticket> tickets;
        if (loggedUser.getRole() == User.Role.REQUESTOR) {
            tickets = ticketRepository.findByCreator(loginService.getLoggedUser());
        }
        else if (loggedUser.getRole() == User.Role.APPROVER) {
            tickets = ticketRepository.findByStatusIn(Arrays.asList(TicketStatus.NEW, TicketStatus.APPROVED));
        }
        else if (loginService.getLoggedUser().getRole() == User.Role.EXECUTOR) {
            tickets = ticketRepository.findByStatusIn(Arrays.asList(TicketStatus.APPROVED));
        }
        else {
            throw new RuntimeException("Unsupported user role");
        }
        for (Ticket t : tickets) {
            if (loggedUser.getUnread().contains(t)) {
                t.setUnread(true);
            }
        }
        return tickets;
    }

    public Ticket find(String projectPrefix, Integer ticketNumber) {
        return this.find(projectPrefix, ticketNumber, false);
    }

    private Ticket find(String projectPrefix, Integer ticketNumber, boolean allowNull) {
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
        return this.save(projectPrefix, ticketNumber, null, data);
    }

    @Transactional
    public Ticket save(String projectPrefix, Integer ticketNumber, String sessionId, Ticket data) throws TicketNotFoundException {
        return internalSaveOrPatch(projectPrefix, ticketNumber, sessionId, data, false);
    }

    @Transactional
    public Ticket patch(String projectPrefix, Integer ticketNumber, Ticket data) throws TicketNotFoundException {
        return internalSaveOrPatch(projectPrefix, ticketNumber, null, data, true);
    }

    private Ticket internalSaveOrPatch(String projectPrefix, Integer ticketNumber, String sessionId, Ticket data, boolean patch) throws TicketNotFoundException {
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
                ticket = new Ticket(project, data);
                Ticket newTicket = ticketRepository.save(ticket);
                Session session = sessionRepository.findOne(sessionId);
                if(session!=null){
                    ticket.resetAttachments(session.getAttachments());
                    session.cleanAttachments();
                    sessionRepository.delete(session);
                }
                this.notificationService.notifyCreation(ticket);
                return newTicket;
            }
        }
        else {
            validateUpdate(ticket);
            ticket.set(data);
            ticket.logUpdate();
            return ticket;
        }
    }

    @Transactional
    public void changePriority(String projectPrefix, Integer ticketNumber, TicketPriority newPriority, String comment) throws TicketNotFoundException {
        Ticket ticket = this.find(projectPrefix, ticketNumber, true);

        if (loginService.getLoggedUser().getRole() != User.Role.APPROVER) {
            throw new SecurityException("Only Approvers can perform this operation");
        }
        if (ticket.getStatus() != TicketStatus.NEW && ticket.getStatus() != TicketStatus.APPROVED) {
            throw new UnsupportedOperationException("Change Priority is not a valid operation for tickets in status " + ticket.getStatus());
        }

        if (ticket.getPriority() != newPriority) {
            ticket.changePriority(newPriority, comment);
            this.notificationService.notifyChangePriority(ticket);
        }
    }

    @Transactional
    public void addComment(String projectPrefix, Integer ticketNumber, String comment) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        validateUpdate(ticket);
        ticket.addComment(comment);
        ticket.logUpdate();
    }

    @Transactional
    public void cancel(String projectPrefix, Integer ticketNumber, String comment) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        if (loginService.getLoggedUser().getRole() == User.Role.REQUESTOR) {
            if (!ticket.createdBy().equals(loginService.getLoggedUser())) {
                throw new SecurityException("Requestors can only cancel tickets they created");
            }
            if (ticket.getStatus() == TicketStatus.NEW) {
                this.notificationService.notifyCreatorCancel(ticket);
                this.ticketRepository.delete(ticket);
            }
            else if (ticket.getStatus() == TicketStatus.APPROVED) {
                if (comment == null || comment.trim().isEmpty()) {
                    throw new IllegalArgumentException("Comment is required in order to cancel an approved ticket");
                }
                this.notificationService.notifyCreatorCancel(ticket);
                ticket.changeStatus(TicketStatus.CANCELLED, comment);
            }
            else {
                throw new UnsupportedOperationException("For Requestors, Cancel is not a valid operation for tickets in status " + ticket.getStatus());
            }
        }
        else if (loginService.getLoggedUser().getRole() == User.Role.APPROVER) {
            if (ticket.getStatus() == TicketStatus.NEW) {
                if (comment == null || comment.trim().isEmpty()) {
                    throw new IllegalArgumentException("Comment is required in order to cancel an approved ticket");
                }
                this.notificationService.notifyApproverCancel(ticket);
                ticket.changeStatus(TicketStatus.CANCELLED, comment);
            }
            else {
                throw new UnsupportedOperationException("For Approvers, Cancel is not a valid operation for tickets in status " + ticket.getStatus());
            }
        }
        else {
            throw new SecurityException("Only Requestor or Approver users can perform this operation");
        }
    }

    @Transactional
    public void reject(String projectPrefix, Integer ticketNumber, String comment) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        if (loginService.getLoggedUser().getRole() != User.Role.APPROVER) {
            throw new SecurityException("Only Approver users can perform this operation");
        }
        if (ticket.getStatus() != TicketStatus.NEW) {
            throw new UnsupportedOperationException("Reject is not a valid operation for tickets in status " + ticket.getStatus());
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment is required in order to cancel an approved ticket");
        }
        ticket.changeStatus(TicketStatus.REJECTED, comment);
        this.notificationService.notifyReject(ticket);
    }
    
    @Transactional
    public void approve(String projectPrefix, Integer ticketNumber, String comment) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        if (loginService.getLoggedUser().getRole() != User.Role.APPROVER) {
            throw new SecurityException("Only Approver users can perform this operation");
        }
        if (ticket.getStatus() != TicketStatus.NEW) {
            throw new UnsupportedOperationException("Approve is not a valid operation for tickets in status " + ticket.getStatus());
        }
        ticket.changeStatus(TicketStatus.APPROVED, comment);
        this.notificationService.notifyApproval(ticket);
    }

    @Transactional
    public void done(String projectPrefix, Integer ticketNumber) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        if (loginService.getLoggedUser().getRole() != User.Role.EXECUTOR) {
            throw new SecurityException("Only Executor users can perform this operation");
        }
        if (ticket.getStatus() != TicketStatus.APPROVED) {
            throw new UnsupportedOperationException("Mark as Done is not a valid operation for tickets in status " + ticket.getStatus());
        }
        ticket.changeStatus(TicketStatus.DONE, null);
        this.notificationService.notifyDone(ticket);
    }

    @Transactional
    public void read(String projectPrefix, Integer ticketNumber) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        loginService.getLoggedUser().read(ticket);
    }

    @Transactional
    public Attachment addAttachment(String projectPrefix, Integer ticketNumber, String fileName, BigDecimal fileSize, String fileType, byte[] bytes) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        validateUpdate(ticket);
        Attachment attachment = ticket.addAttachment(fileName, fileSize, fileType, bytes);
        ticket.logUpdate();
        return attachment;
    }

    @Transactional
    public Attachment addAttachmentForSession(String sessionId, String fileName, BigDecimal fileSize, String fileType, byte[] bytes) {
        Session session = sessionRepository.findOne(sessionId);
        if (session == null) {
            session = new Session(sessionId);
            session = sessionRepository.save(session);
        }
        return session.addAttachment(fileName, fileSize, fileType, bytes);
    }

    public Attachment getAttachment(String projectPrefix, Integer ticketNumber, Long attachmentId) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        return ticket.findAttachmentById(attachmentId);
    }

    public Attachment getAttachmentForSession(String sessionId, Long attachmentId) {
        Session session = sessionRepository.findOne(sessionId);
        if (session != null) {
            return session.findAttachmentById(attachmentId);
        }
        return null;
    }

    @Transactional
    public void deleteAttachment(String projectPrefix, Integer ticketNumber, Long attachmentId) {
        Ticket ticket = this.find(projectPrefix, ticketNumber);
        validateUpdate(ticket);
        ticket.removeAttachment(attachmentId);
        ticket.logUpdate();
    }

    @Transactional
    public void deleteAttachmentForSession(String sessionId, Long attachmentId) {
        Session session = sessionRepository.findOne(sessionId);
        if (session != null) {
            session.removeAttachment(attachmentId);
        }
    }

    @Transactional
    public void cleanupAttachmentStore(String sessionId) {
        Session session = sessionRepository.findOne(sessionId);
        if (session != null) {
            session.cleanAttachments();
        }
    }

    public Set<Attachment> getAttachmentsForSession(String sessionId) {
        Session session = sessionRepository.findOne(sessionId);
        if (session != null) {
            return session.getAttachments();
        }
        else {
            return Collections.EMPTY_SET;
        }
    }

    private void validateUpdate(Ticket ticket) {
        if (!ticket.createdBy().equals(loginService.getLoggedUser())) {
            throw new SecurityException("Only the creator can edit a ticket");
        }
        if (ticket.getStatus() != TicketStatus.NEW) {
            throw new UnsupportedOperationException("Edit is not a valid operation for tickets in status " + ticket.getStatus());
        }
    }

}
