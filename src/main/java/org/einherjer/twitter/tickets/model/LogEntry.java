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

import org.einherjer.twitter.tickets.ServiceLocator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "LogEntries") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
public abstract class LogEntry extends AbstractEntity implements Comparable<LogEntry> {

    @JsonIgnore //needed in bidirectional association to prevent infinite loop while serializing
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private @Getter Ticket ticket;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(nullable = false)
    protected DateTime timestamp;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    /**
     * cannot use loginService in no args constructor cause Hibernate calls the no args constructor during context initialization
     * (apparently because of the @Inheritance annotation) creating a loop (cause loginService depends on entityManager as well)
     */
    protected void init(Ticket ticket) {
        this.ticket = ticket;
        this.timestamp = new DateTime();
        this.user = ServiceLocator.getInstance().getLoginService().getLoggedUser();
    }

    /**
     * returns the user that performed the action, only if the action is "creation"
     */
    public abstract User createdBy();

    @Override
    public int compareTo(LogEntry o) {
        return this.timestamp.compareTo(o.getTimestamp());
    }

}
