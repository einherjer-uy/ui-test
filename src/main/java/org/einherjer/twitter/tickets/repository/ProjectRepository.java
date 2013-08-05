package org.einherjer.twitter.tickets.repository;

import org.einherjer.twitter.tickets.model.Project;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.repository.annotation.RestResource;

@RestResource(path = "project", rel = "project")
public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {

    //All query method resources are exposed under the resource "search"
    //@Param required to be able to GET /projects/search/findByPrefix?prefix=PR1
    Project findByPrefix(@Param("prefix") String prefix);

    //findAll : GET /project
    //findOne : GET /project/{id}
    //PagingAndSorting allows: GET /project?limit=5 and GET &/project?limit=5&page=3
    //create  : POST /project | Headers: Content-Type:application/json | Body: <json> | (Result: 201 Created)
    //delete  : DELETE /project/{id} | (Result: 204 No Content)
    //update  : PUT /project/{id} | Headers: Content-Type:application/json | Body: <json> | (Result: 204 No Content)

}
