package org.einherjer.twitter.tickets.repository;

import org.einherjer.twitter.tickets.model.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, String> {

}
