package org.einherjer.twitter.tickets.repository;

import org.einherjer.twitter.tickets.model.EmailAddress;
import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.model.User.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.repository.annotation.RestResource;

@RestResource(path = "user", rel = "user")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(EmailAddress username);
    
    Iterable<User> findByRole(Role role);

}
