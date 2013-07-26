package org.einherjer.twitter.tickets.repository;

import org.einherjer.twitter.tickets.model.Project;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Project findByPrefix(String prefix);

}
