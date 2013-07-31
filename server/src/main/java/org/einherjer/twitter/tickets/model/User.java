package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Users") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class User extends AbstractEntity {

    @Column(unique = true)
    private EmailAddress username;

    @JsonIgnore
    private Password password;

    public User(String username, String password) {
        this.username = new EmailAddress(username);
        this.password = new Password(password);
    }

}
