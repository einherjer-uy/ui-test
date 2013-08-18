package org.einherjer.twitter.tickets.service;

import org.einherjer.twitter.tickets.model.EmailAddress;
import org.einherjer.twitter.tickets.model.InvalidLoginException;
import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    //This method is not used since the login is handled by spring security.
    //    @Transactional(readOnly = true)
    public User validateLogin(String username, String password) throws InvalidLoginException {
        User user = userRepository.findByUsername(new EmailAddress(username));
        if (user == null) {
            throw new InvalidLoginException("User not in database");
        }
        user.getPassword().validateLogin(password);
        return user;
    }

    public User getLoggedUser() {
        org.springframework.security.core.userdetails.User u = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedUser = userRepository.findByUsername(new EmailAddress(u.getUsername()));
        Assert.notNull(loggedUser, "Error while retrieving logged user");
        return loggedUser;
    }

}
