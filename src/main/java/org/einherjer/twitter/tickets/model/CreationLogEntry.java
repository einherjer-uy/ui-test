package org.einherjer.twitter.tickets.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "CreationLogEntries") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class CreationLogEntry extends LogEntry {

    public CreationLogEntry(User user) {
        super(user);
    }

}
