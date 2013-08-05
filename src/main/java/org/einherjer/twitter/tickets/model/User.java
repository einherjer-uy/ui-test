package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Users") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class User extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private EmailAddress username;

    @JsonIgnore
    @Column(nullable = false)
    private @Setter Password password;

    @Column(nullable = false)
    private @Setter Role role;
    
    public User(String username, String password, Role role) {
        this.username = new EmailAddress(username);
        this.password = new Password(password);
        this.role = role;
    }

    public static enum Role {
        REQUESTOR,
        APPROVER,
        EXECUTOR;
    }
    
}
