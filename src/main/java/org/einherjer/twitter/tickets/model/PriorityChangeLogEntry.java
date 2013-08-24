package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.einherjer.twitter.tickets.model.Ticket.TicketPriority;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "PriorityChangeLogEntries")
public class PriorityChangeLogEntry extends CommentLogEntry {

    @Column(nullable = false)
    private TicketPriority newPriority;

    /**
     * see LogEntry.init javadoc
     */
    public static PriorityChangeLogEntry create(Ticket ticket, TicketPriority newPriority, String comment) {
        PriorityChangeLogEntry e = new PriorityChangeLogEntry();
        e.init(ticket, comment);
        e.newPriority = newPriority;
        return e;
    }

}
