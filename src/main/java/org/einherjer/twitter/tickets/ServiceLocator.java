package org.einherjer.twitter.tickets;

import org.einherjer.twitter.tickets.repository.TicketRepository;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.einherjer.twitter.tickets.service.LoginService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public final class ServiceLocator implements ApplicationContextAware {

    private static ServiceLocator instance = new ServiceLocator();

    private ApplicationContext context;

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        return ServiceLocator.instance;
    }

    public void setApplicationContext(ApplicationContext context) {
        ServiceLocator.instance.context = context;
    }

    public TicketRepository getTicketRepository() {
        return context.getBean("ticketRepository", TicketRepository.class);
    }

    public UserRepository getUserRepository() {
        return context.getBean("userRepository", UserRepository.class);
    }

    public LoginService getLoginService() {
        return context.getBean("loginService", LoginService.class);
    }

}
