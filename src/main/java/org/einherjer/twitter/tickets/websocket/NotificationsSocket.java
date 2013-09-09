package org.einherjer.twitter.tickets.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.einherjer.twitter.tickets.ServiceLocator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebSocket
public class NotificationsSocket {
    
    //    @OnWebSocketConnect
    //    public void onConnect(Session session) {
    //        
    //    }

    @OnWebSocketMessage
    public void onMessage(Session session, String text) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SocketMessage message = objectMapper.readValue(text, SocketMessage.class);
        switch(message.method){
            case LOGIN:
                ServiceLocator.getInstance().getNotificationsWebSocketPool().addSession(message.userName, session);
                break;
            case LOGOUT:
                ServiceLocator.getInstance().getNotificationsWebSocketPool().removeSession(message.userName, session);
                break;
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        ServiceLocator.getInstance().getNotificationsWebSocketPool().removeSession(session);
    }
    
    public static class SocketMessage {
        public SocketMessageMethod method;
        public String userName;
    }
    
    private static enum SocketMessageMethod {
        LOGIN,
        LOGOUT;
    }
}