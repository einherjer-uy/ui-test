package org.einherjer.twitter.tickets;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

//Several WebApplicationInitializer can coexist (see TwitterTicketsWebApplicationInitializer)
//AbstractSecurityWebApplicationInitializer registers the filter needed by Spring Security
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

}
