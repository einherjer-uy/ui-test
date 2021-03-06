package org.einherjer.twitter.tickets.repository;

import org.einherjer.twitter.tickets.model.Project;
import org.einherjer.twitter.tickets.model.Ticket;
import org.einherjer.twitter.tickets.model.Ticket.TicketStatus;
import org.einherjer.twitter.tickets.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.repository.annotation.RestResource;

@RestResource(path = "ticket", rel = "ticket") //TODO: not working, still publishes /tickets (plural)
public interface TicketRepository extends PagingAndSortingRepository<Ticket, Long> {

    @Query("select max(t.number) from Ticket t where t.project = ?1")
    Integer getMaxTicketNumberByProject(Project project);

    Ticket findByProjectAndNumber(Project project, Integer ticketNumber);

    @Query("select l.ticket from CreationLogEntry l where l.user = ?1 order by l.timestamp desc")
    Iterable<Ticket> findByCreator(User user);

    @Query("select l.ticket from CreationLogEntry l where l.ticket.status in (?1) order by l.timestamp desc")
    Iterable<Ticket> findByStatusIn(Iterable<TicketStatus> statuses);

    //override CrudRepository methods and hide them from the rest exporter with exporter = false
    //TODO: for some reason adding @RestResource(exported = false) makes the call to the repository fail even from another Service  
    /*@Override
    @RestResource(exported = false)
    <S extends Ticket> S save(S entity);

    @Override
    @RestResource(exported = false)
    <S extends Ticket> Iterable<S> save(Iterable<S> entities);

    @Override
    @RestResource(exported = false)
    void delete(Long id);

    @Override
    @RestResource(exported = false)
    void delete(Ticket entity);

    @Override
    @RestResource(exported = false)
    void delete(Iterable<? extends Ticket> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();*/
}
