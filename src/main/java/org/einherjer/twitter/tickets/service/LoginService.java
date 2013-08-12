package org.einherjer.twitter.tickets.service;

import org.einherjer.twitter.tickets.model.EmailAddress;
import org.einherjer.twitter.tickets.model.InvalidLoginException;
import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
