package org.einherjer.twitter.tickets.loader;

import lombok.extern.slf4j.Slf4j;

import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.model.User.Role;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@DependsOn("serviceLocator")
@Slf4j
public class UserLoader {

    @Autowired
    public UserLoader(UserRepository repository) {
        Assert.notNull(repository, "UserRepository must not be null!");

        if (repository.count() > 0) {
            return;
        }

        User u = new User("user@twitter.com", "Admin_123", Role.REQUESTOR);
        repository.save(u);
        log.info("Created User " + u);

        u = new User("req@twitter.com", "Admin_123", Role.REQUESTOR);
        repository.save(u);
        log.info("Created User " + u);

        u = new User("app@twitter.com", "Admin_123", Role.APPROVER);
        repository.save(u);
        log.info("Created User " + u);

        u = new User("exe@twitter.com", "Admin_123", Role.EXECUTOR);
        repository.save(u);
        log.info("Created User " + u);
    }

}
