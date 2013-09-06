package org.einherjer.twitter.tickets.websocket;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;



//Websocket used just for testing the technology, actually in this case the client just receives the notifications but doesn't send anything, so
//a more appropriate technology could have been EventSource aka ServerSentEvents

//Reference EventSource / ServerSentEvents

//http://www.html5rocks.com/en/tutorials/eventsource/basics/
//http://stackoverflow.com/questions/10079573/server-sent-events-java-with-spring-mvc
//http://stackoverflow.com/questions/16412655/server-sent-events-using-server-side-as-servlets
//https://developer.mozilla.org/en-US/docs/Server-sent_events/Using_server-sent_events

//Reference Websockets

//http://webtide.intalio.com/2012/10/jetty-9-updated-websocket-api/
//http://www.eclipse.org/jetty/documentation/current/websocket-jetty.html
//http://www.html5rocks.com/en/tutorials/websockets/basics/
//http://angelozerr.wordpress.com/2011/07/26/websockets_jetty_step3/
//http://blog.springsource.org/2013/05/22/spring-framework-4-0-m1-websocket-support/
//http://blog.springsource.org/2013/07/24/spring-framework-4-0-m2-websocket-messaging-architectures/

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/tt/notifications" })
//NOTE that this works as a regular servlet so SPRING SECURITY is also able to secure this websocket (even though we are using jetty API and not spring's)
public class NotificationsSocketServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(5 * 60 * 1000); //5 minute timeout
        factory.register(NotificationsSocket.class);
    }
}