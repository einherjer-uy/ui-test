package org.einherjer.twitter.tickets.loader;

import lombok.extern.slf4j.Slf4j;

import org.einherjer.twitter.tickets.model.Project;
import org.einherjer.twitter.tickets.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@DependsOn("serviceLocator")
@Slf4j
class ProjectLoader {

    public static final String PR1_PREFIX = "PR1";
    public static final String PR2_PREFIX = "PR2";

    @Autowired
    public ProjectLoader(ProjectRepository repository) {
        Assert.notNull(repository, "ProjectRepository must not be null!");

        if (repository.count() > 0) {
            return;
        }

        Project p1 = new Project(PR1_PREFIX, "Project 1");
        repository.save(p1);
        log.info("Created Project " + p1);

        Project p2 = new Project(PR2_PREFIX, "Project 2");
        repository.save(p2);
        log.info("Created Project " + p2);
    }

}
