package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "CommentLogEntries") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class CommentLogEntry extends LogEntry {

    @Column(nullable = true)
    private String text;

    protected void init(String text) {
        super.init();
        this.text = text;
    }

    /**
     * see LogEntry.init javadoc
     */
    public static CommentLogEntry create(String text) {
        CommentLogEntry e = new CommentLogEntry();
        e.init(text);
        return e;
    }

    @Override
    public User createdBy() {
        return null;
    }

}
