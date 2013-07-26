package org.einherjer.twitter.tickets.loader;

import lombok.extern.slf4j.Slf4j;

import org.einherjer.twitter.tickets.model.Project;
import org.einherjer.twitter.tickets.model.Ticket;
import org.einherjer.twitter.tickets.repository.ProjectRepository;
import org.einherjer.twitter.tickets.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@DependsOn("serviceLocator")
@Slf4j
public class TicketLoader {

    @Autowired
    public TicketLoader(TicketRepository ticketRepository, ProjectRepository projectRepository) {
        Assert.notNull(ticketRepository, "TicketRepository must not be null!");
        Assert.notNull(projectRepository, "ProjectRepository must not be null!");

        if (ticketRepository.count() > 0) {
            return;
        }

        Project p1 = projectRepository.findByPrefix(ProjectLoader.PR1_PREFIX);
        Ticket t1 = new Ticket(p1, "Some title", "Some description");
        t1.addComment("Hello World!");
        ticketRepository.save(t1);
        log.info("Created Ticket " + t1);

        Project p2 = projectRepository.findByPrefix(ProjectLoader.PR2_PREFIX);
        Ticket t2 = new Ticket(p2, "Some title 2", "Some description 2");
        t2.addComment("Hello World 2!");
        ticketRepository.save(t2);
        log.info("Created Ticket " + t2);
    }

}
