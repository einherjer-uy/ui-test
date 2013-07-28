package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User extends AbstractEntity {

    @Column(unique = true)
    private EmailAddress username;

    private Password password;

    public User(String username, String password) {
        this.username = new EmailAddress(username);
        this.password = new Password(password);
    }

}
