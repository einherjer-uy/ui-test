package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.einherjer.twitter.tickets.ServiceLocator;
import org.springframework.util.Assert;

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

    //TODO: move to service
    public static void validateLogin(String username, String password) {
        User user = ServiceLocator.getInstance().getUserRepository().findByUsername(username);
        Assert.notNull(user, "User not in database");
        user.password.validateLogin(password);
    }

}
