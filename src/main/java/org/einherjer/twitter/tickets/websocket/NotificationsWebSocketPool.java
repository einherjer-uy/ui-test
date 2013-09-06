package org.einherjer.twitter.tickets.websocket;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.einherjer.twitter.tickets.model.User.Role;
import org.springframework.stereotype.Component;

@Component
public final class NotificationsWebSocketPool {

    private Set<Session> approverPool = new HashSet<Session>();
    private Set<Session> executorPool = new HashSet<Session>();
    private Map<String, Set<Session>> requestorPool = new HashMap<String, Set<Session>>();

    public void addSession(Role role, String userName, Session session) {
        switch (role) {
            case REQUESTOR:
                Set<Session> requestorSessions = requestorPool.get(userName);
                if (requestorSessions == null) {
                    requestorSessions = new HashSet<Session>();
                    requestorPool.put(userName, requestorSessions);
                }
                requestorSessions.add(session);
                break;
            case APPROVER:
                approverPool.add(session);
                break;
            case EXECUTOR:
                executorPool.add(session);
                break;
        }
    }

    public void removeSession(Session session) {
        approverPool.remove(session);
        executorPool.remove(session);
        //TODO: not the more intelligent approach
        for (Set<Session> requestorSessions : requestorPool.values()) {
            requestorSessions.remove(session);
        }
    }

    public Set<Session> getApproverSessions() {
        return Collections.unmodifiableSet(approverPool);
    }

    public Set<Session> getExecutorSessions() {
        return Collections.unmodifiableSet(executorPool);
    }

    public Set<Session> getRequestorSessions(String userName) {
        Set<Session> requestorSessions = requestorPool.get(userName);
        if (requestorSessions == null) {
            return null;
        }
        return Collections.unmodifiableSet(requestorSessions);
    }

}
