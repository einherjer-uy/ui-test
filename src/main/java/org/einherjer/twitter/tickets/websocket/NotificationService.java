package org.einherjer.twitter.tickets.websocket;

import java.io.IOException;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.jetty.websocket.api.Session;
import org.einherjer.twitter.tickets.model.User.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@Slf4j
@Service
public final class NotificationService {

    @Autowired
    private NotificationsWebSocketPool pool;

    public void broadcast(Role role) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule() {
            {
                addSerializer(NotificationType.class, new StdSerializer<NotificationType>(NotificationType.class) {
                    @Override
                    public void serialize(NotificationType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
                        switch (value) {
                            case NOTICE:
                                jgen.writeString("");
                                break;
                            case INFO:
                                jgen.writeString("info");
                                break;
                            case SUCCESS:
                                jgen.writeString("sucess");
                                break;
                            case ERROR:
                                jgen.writeString("error");
                                break;
                            default:
                                throw new UnsupportedOperationException("method not implemented for value: " + this);
                        }
                        
                    }
                });
            }
        });
        switch (role) {
            case APPROVER:
                try {
                    this.send(pool.getApproverSessions(), objectMapper.writeValueAsString(new Notification(NotificationType.INFO, "title", "text")));
                }
                catch (JsonProcessingException e) {
                    log.error("Unexpected error", e);
                    throw new RuntimeException("Unexpected error", e);
                }
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

    @AllArgsConstructor
    public static class Notification {
        public NotificationType type;
        public String title;
        public String text;
    }
    
    private static enum NotificationType {
        NOTICE,
        INFO,
        SUCCESS,
        ERROR;

        public String toString(){
            switch(this){
                case NOTICE:
                    return "";
                case INFO:
                    return "info";
                case SUCCESS:
                    return "success";
                case ERROR:
                    return "error";
                default:
                    throw new UnsupportedOperationException("method not implemented for value: " + this);
            }
        }
    }
    
}
