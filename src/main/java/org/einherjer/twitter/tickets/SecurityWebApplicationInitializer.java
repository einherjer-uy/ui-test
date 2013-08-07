package org.einherjer.twitter.tickets;

import org.springframework.core.annotation.Order;

//Several WebApplicationInitializer can coexist (see TwitterTicketsWebApplicationInitializer)
//AbstractSecurityWebApplicationInitializer registers the filter needed by Spring Security
@Order(2) //see javadoc in AbstractSecurityWebApplicationInitializer
public class SecurityWebApplicationInitializer /* extends AbstractSecurityWebApplicationInitializer*/{

}
