package org.einherjer.twitter.tickets;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/* Regular SpringMVC web initializer with spring-data-rest (RepositoryRestExporterServlet) or vanilla (DispatcherServlet)
public class TwitterTicketsWebApplicationInitializer implements WebApplicationInitializer {

@Override
public void onStartup(ServletContext container) throws ServletException {

AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
rootContext.register(ApplicationConfig.class);

container.addListener(new ContextLoaderListener(rootContext));

DispatcherServlet servlet = new RepositoryRestExporterServlet(); //use just new DispatcherServlet for regular spring mvc (manual Controllers, no spring-data-rest magic)
ServletRegistration.Dynamic dispatcher = container.addServlet("spring-data-rest-exporter", servlet);
dispatcher.setLoadOnStartup(1);
dispatcher.addMapping("/");
}*/

/**
 * Servlet 3.0 {@link WebApplicationInitializer} using Spring 3.2 convenient base class
 * {@link AbstractAnnotationConfigDispatcherServletInitializer}. It essentially sets up a root application context from
 * {@link ApplicationConfig}, and a dispatcher servlet application context from {@link RepositoryRestMvcConfiguration}
 * (enabling Spring Data REST) and {@link WebConfiguration} for general Spring MVC customizations.
 * 
 * @author Oliver Gierke
 */
public class TwitterTicketsWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /* 
     * (non-Javadoc)
     * @see org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer#getRootConfigClasses()
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { ApplicationConfig.class };
    }

    /* 
     * (non-Javadoc)
     * @see org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer#getServletConfigClasses()
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebConfiguration.class };
    }

    /* 
     * (non-Javadoc)
     * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer#getServletMappings()
     */
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    /* 
     * (non-Javadoc)
     * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer#getServletFilters()
     */
    @Override
    protected javax.servlet.Filter[] getServletFilters() {
        return new javax.servlet.Filter[] { new OpenEntityManagerInViewFilter() };
    }

    /**
     * Web layer configuration enabling Spring MVC, Spring Hateoas hypermedia support.
     * 
     * @author Oliver Gierke
     */
    @Configuration
    @EnableHypermediaSupport
    //no need for @EnableWebMvc cause the superclass (DelegatingWebMvcConfiguration) performs the same process
    //  (plus, it allows to override some methods without extending WebMvcConfigurerAdapter)  
    @Import(RepositoryRestMvcConfiguration.class)
    @ComponentScan(excludeFilters = @Filter({ Service.class, Configuration.class }))
    public static class WebConfiguration extends DelegatingWebMvcConfiguration {

        /*
         * (non-Javadoc)
         * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#configureContentNegotiation(org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer)
         */
        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.defaultContentType(MediaType.APPLICATION_JSON);
        }

        private ObjectMapper objectMapper() {
            Jackson2ObjectMapperFactoryBean bean = new Jackson2ObjectMapperFactoryBean();
            bean.setIndentOutput(true);
            bean.setSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            bean.afterPropertiesSet();
            ObjectMapper objectMapper = bean.getObject();
            objectMapper.registerModule(new JodaModule());
            return objectMapper;
        }

        private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setObjectMapper(objectMapper());
            return converter;
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(mappingJackson2HttpMessageConverter());
        }

        @Bean
        public MultipartResolver multipartResolver() {
            CommonsMultipartResolver bean = new CommonsMultipartResolver();
            bean.setMaxUploadSize(5242880);
            return bean;
        }

    }
}

