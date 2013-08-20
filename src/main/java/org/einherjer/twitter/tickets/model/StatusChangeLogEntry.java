package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.einherjer.twitter.tickets.model.Ticket.TicketStatus;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "StatusChangeLogEntries") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class StatusChangeLogEntry extends CommentLogEntry {

    @Column(nullable = false)
    private TicketStatus newStatus;

    /**
     * see LogEntry.init javadoc
     */
    public static StatusChangeLogEntry create(Ticket ticket, TicketStatus newStatus, String comment) {
        StatusChangeLogEntry e = new StatusChangeLogEntry();
        e.init(ticket, comment);
        e.newStatus = newStatus;
        return e;
    }

}
