package org.einherjer.twitter.tickets.controller;

import lombok.Getter;
import lombok.Setter;

/*
 * Represents the form data (x-www-from-urlencoded) in a POST /login
 * SSL assumed
 */
@Getter
@Setter
public class LoginForm {

    private String username;
    private String password;

}
