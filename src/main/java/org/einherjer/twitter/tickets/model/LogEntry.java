package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "LogEntries") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class LogEntry extends AbstractEntity implements Comparable<LogEntry> {

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(nullable = false)
    private DateTime timestamp;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public LogEntry(User user) {
        this.timestamp = new DateTime();
        this.user = user;
    }

    @Override
    public int compareTo(LogEntry o) {
        return this.timestamp.compareTo(o.getTimestamp());
    }

}
