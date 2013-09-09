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
        u.setFirstName("Samuel L.");
        u.setLastName("Jackson");
        repository.save(u);
        log.info("Created User " + u);

        u = new User("req@twitter.com", "Admin_123", Role.REQUESTOR);
        u.setFirstName("Paul");
        u.setLastName("Newman");
        repository.save(u);
        log.info("Created User " + u);

        u = new User("req2@twitter.com", "Admin_123", Role.REQUESTOR);
        u.setFirstName("Bruce");
        u.setLastName("Willis");
        repository.save(u);
        log.info("Created User " + u);

        u = new User("app@twitter.com", "Admin_123", Role.APPROVER);
        u.setFirstName("Chuck");
        u.setLastName("Norris");
        repository.save(u);
        log.info("Created User " + u);

        u = new User("app2@twitter.com", "Admin_123", Role.APPROVER);
        u.setFirstName("Mr.");
        u.setLastName("T");
        repository.save(u);
        log.info("Created User " + u);

        u = new User("exe@twitter.com", "Admin_123", Role.EXECUTOR);
        u.setFirstName("Clint");
        u.setLastName("Eastwood");
        repository.save(u);
        log.info("Created User " + u);

        u = new User("exe3@twitter.com", "Admin_123", Role.EXECUTOR);
        u.setFirstName("Lee");
        u.setLastName("Van Cleef");
        repository.save(u);
        log.info("Created User " + u);
    }

}
