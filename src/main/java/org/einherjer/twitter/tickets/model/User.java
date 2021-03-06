package org.einherjer.twitter.tickets.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@NoArgsConstructor
@Table(name = "Users") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
@EqualsAndHashCode(callSuper = false, of = "username")
@ToString(of = "username")
public class User extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private @Getter EmailAddress username;

    @JsonIgnore
    @Column(nullable = false)
    private @Getter @Setter Password password;

    @Column(nullable = false)
    private @Getter @Setter Role role;
    
    @Column(nullable = false)
    private @Getter @Setter DashboardMode dashboardMode = DashboardMode.LIST;
    
    private @Getter @Setter String firstName;
    private @Getter @Setter String lastName;
    
    @Column(nullable = false)
    private @Getter boolean notificationsEnabled = true;

    @JsonIgnore
    @ManyToMany
    private Set<Ticket> unread = new HashSet<Ticket>();

    public User(String username, String password, Role role) {
        this.username = new EmailAddress(username);
        this.password = new Password(password);
        this.role = role;
    }

    @JsonIgnore(false) //override the getter just to include the id in the json
    @Override
    public Long getId() {
        return super.getId();
    }

    public Set<Ticket> getUnread() {
        return Collections.unmodifiableSet(this.unread);
    }

    public void addUnread(Ticket ticket) {
        this.unread.add(ticket);
    }

    public void read(Ticket ticket) {
        this.unread.remove(ticket);
    }

    public static enum Role {
        REQUESTOR,
        APPROVER,
        EXECUTOR;
    }
    
    public static enum DashboardMode {
        LIST,
        CARD;
    }

}
