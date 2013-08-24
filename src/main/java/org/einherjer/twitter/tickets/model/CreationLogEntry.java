package org.einherjer.twitter.tickets.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "CreationLogEntries") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class CreationLogEntry extends LogEntry {

    /**
     * see LogEntry.init javadoc
     */
    public static CreationLogEntry create(Ticket ticket) {
        CreationLogEntry e = new CreationLogEntry();
        e.init(ticket);
        return e;
    }

    @Override
    public User createdBy() {
        return this.user;
    }

}
