package org.einherjer.twitter.tickets.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "CommentLogEntries") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class CommentLogEntry extends LogEntry {

    private String text;

    public CommentLogEntry(User user, String text) {
        super(user);
        this.text = text;
    }

}
