package org.einherjer.twitter.tickets.repository;

import org.einherjer.twitter.tickets.model.Project;
import org.einherjer.twitter.tickets.model.Ticket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, Long> {

    @Query("select max(t.number) from Ticket t where t.project = ?1")
    Integer getMaxTicketNumberByProject(Project project);

    Ticket findByProjectAndNumber(Project project, Integer ticketNumber);

}
