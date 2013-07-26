package org.einherjer.twitter.tickets;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.data.rest.webmvc.RepositoryRestExporterServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class TwitterTicketsWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ApplicationConfig.class);

        container.addListener(new ContextLoaderListener(rootContext));

        DispatcherServlet servlet = new RepositoryRestExporterServlet();
        ServletRegistration.Dynamic dispatcher = container.addServlet("exporter", servlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
    }
}
