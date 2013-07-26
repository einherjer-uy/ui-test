package org.einherjer.twitter.tickets.repository;

import org.einherjer.twitter.tickets.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

}
