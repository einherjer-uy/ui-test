package org.einherjer.twitter.tickets.repository;

import org.einherjer.twitter.tickets.model.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {

}
