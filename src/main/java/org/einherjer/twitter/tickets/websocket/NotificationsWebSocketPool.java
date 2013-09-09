package org.einherjer.twitter.tickets.websocket;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.einherjer.twitter.tickets.model.EmailAddress;
import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.model.User.Role;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class NotificationsWebSocketPool {

    @Autowired
    private UserRepository userRepository;

    private Map<User, Set<Session>> pool = new HashMap<User, Set<Session>>(); //set of sessions by user (the same user can have several tabs open with different sockets)

    public void addSession(String username, Session session) {
        User user = userRepository.findByUsername(new EmailAddress(username));
        Set<Session> userSessions = pool.get(user);
        if (userSessions == null) {
            userSessions = new HashSet<Session>();
            pool.put(user, userSessions);
        }
        userSessions.add(session);
    }

    public void removeSession(String username, Session session) {
        User user = userRepository.findByUsername(new EmailAddress(username));
        pool.get(user).remove(session);
        if (pool.get(user).isEmpty()) {
            pool.remove(user); //allows the user to logout all his sessions so in his next login (addSession) we will read the user data (including on/off notifications setting) from the database again and put it in the map
        }
    }

    public void removeSession(Session session) {
        for (Entry<User, Set<Session>> entry : pool.entrySet()) {
            entry.getValue().remove(session);
            if (entry.getValue().isEmpty()) {
                pool.remove(entry.getKey()); //allows the user to logout all his sessions so in his next login (addSession) we will read the user data (including on/off notifications setting) from the database again and put it in the map
            }
        }
    }

    public Set<Session> getApproverSessions() {
        return this.getSessionsByRole(Role.APPROVER);
    }

    public Set<Session> getExecutorSessions() {
        return this.getSessionsByRole(Role.EXECUTOR);
    }

    private Set<Session> getSessionsByRole(Role role) {
        Set<Session> sessions = new HashSet<Session>();
        for (User user : pool.keySet()) {
            if (user.getRole().equals(role) && user.isNotificationsEnabled()) {
                sessions.addAll(pool.get(user));
            }
        }
        return Collections.unmodifiableSet(sessions);
    }

    public Set<Session> getRequestorSessions(User user) {
        for (User userInPool : pool.keySet()) {
            if (userInPool.equals(user) && userInPool.isNotificationsEnabled()) { //use the same criteria for requestors as for approvers/executors (getSessionsByRole) in the sense that the isNotificationsEnable value used is the one at the time the user was added to the pool
                return Collections.unmodifiableSet(pool.get(userInPool));
            }
        }
        return Collections.unmodifiableSet(new HashSet<Session>());
    }

}
