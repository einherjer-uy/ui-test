package org.einherjer.twitter.tickets.loader;

import lombok.extern.slf4j.Slf4j;

import org.einherjer.twitter.tickets.model.EmailAddress;
import org.einherjer.twitter.tickets.model.Project;
import org.einherjer.twitter.tickets.model.Ticket;
import org.einherjer.twitter.tickets.model.Ticket.TicketPriority;
import org.einherjer.twitter.tickets.model.Ticket.TicketType;
import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.repository.ProjectRepository;
import org.einherjer.twitter.tickets.repository.TicketRepository;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@DependsOn({ "serviceLocator", "userLoader", "projectLoader"})
@Slf4j
public class TicketLoader {

    @Autowired
    public TicketLoader(TicketRepository ticketRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        Assert.notNull(ticketRepository, "TicketRepository must not be null!");
        Assert.notNull(projectRepository, "ProjectRepository must not be null!");
        Assert.notNull(userRepository, "UserRepository must not be null!");

        if (ticketRepository.count() > 0) {
            return;
        }

        User u = userRepository.findByUsername(new EmailAddress("user@twitter.com"));

        Project p = projectRepository.findByPrefix(ProjectLoader.TT_PREFIX);

        Ticket data = new Ticket(p, "Some title", "Some description", u, TicketType.APPLICATIONS, TicketPriority.LOW, null);
        Ticket t = new Ticket(p, data, u);
        t.addComment("Hello World!", u);
        ticketRepository.save(t);
        log.info("Created Ticket " + t);

        data = new Ticket(p, "Some title 2", "Some description 2", u, TicketType.HARDWARE, TicketPriority.MEDIUM, new DateTime());
        t = new Ticket(p, data, u);
        t.addComment("Hello World 2!", u);
        ticketRepository.save(t);
        log.info("Created Ticket " + t);
    }

}
