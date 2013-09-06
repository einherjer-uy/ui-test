package org.einherjer.twitter.tickets.websocket;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.jetty.websocket.api.Session;
import org.einherjer.twitter.tickets.model.User.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class NotificationService {

    @Autowired
    private NotificationsWebSocketPool pool;

    public void broadcast(Role role) {
        switch (role) {
            case APPROVER:
                this.send(pool.getApproverSessions(), "There's a new ticket");
                break;
            case EXECUTOR:
                this.send(pool.getExecutorSessions(), "There's a new approved ticket");
                break;
            default:
                throw new UnsupportedOperationException("Invalid operation for role: " + role);
        }
    }

    public void broadcast(String userName, String message) {
        Set<Session> sessions = pool.getRequestorSessions(userName);
        if (sessions == null) {
            log.info("No sessions found for user: " + userName + ". It might be offline");
        }
        else {
            this.send(sessions, message);
        }
    }

    private void send(Set<Session> sessions, String message) {
        for (Session session : sessions) {
            this.send(session, message);
        }
    }

    private void send(Session session, String message) {
        if (session.isOpen()) {
            session.getRemote().sendStringByFuture(message);
        }
    }

}
