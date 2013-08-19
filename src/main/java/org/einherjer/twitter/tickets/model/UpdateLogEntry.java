package org.einherjer.twitter.tickets.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "UpdateLogEntries")
public class UpdateLogEntry extends LogEntry {

    /**
     * see LogEntry.init javadoc
     */
    public static UpdateLogEntry create() {
        UpdateLogEntry e = new UpdateLogEntry();
        e.init();
        return e;
    }

    @Override
    public User createdBy() {
        return null;
    }

}
