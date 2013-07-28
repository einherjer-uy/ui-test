package org.einherjer.twitter.tickets.controller;

import org.einherjer.twitter.tickets.model.Ticket;
import org.springframework.data.rest.repository.context.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener to reject DELETE requests to Spring Data REST.
 * 
 */
@Component
class TicketControllerEventListener extends AbstractRepositoryEventListener<Ticket> {

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.rest.repository.context.AbstractRepositoryEventListener#onBeforeDelete(java.lang.Object)
	 */
	@Override
    protected void onBeforeDelete(Ticket ticket) {
        throw new UnsupportedOperationException("Delete of tickets is not allowed.");
    }

}