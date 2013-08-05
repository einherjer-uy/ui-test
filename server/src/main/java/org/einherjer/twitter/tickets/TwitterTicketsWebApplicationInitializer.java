package org.einherjer.twitter.tickets;

import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

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
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

//Another option would be to extend AbstractAnnotationConfigDispatcherServletInitializer
//and override getRootConfigClasses, getServletConfigClasses, getServletMappings, getServletFilters
//This is a more convenient way to setup spring mvc but doesn't allow to add additional servlets filters 
public class TwitterTicketsWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ApplicationConfig.class);

        container.addListener(new ContextLoaderListener(rootContext));

        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfiguration.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);
        ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcherServlet", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/api/");

        FilterRegistration.Dynamic openEntityManagerInView = container.addFilter("openEntityManagerInViewFilter", new OpenEntityManagerInViewFilter());
        openEntityManagerInView.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE), false, "dispatcherServlet");

    }

    /**
     * Web layer configuration enabling Spring MVC, Spring Hateoas hypermedia support.
     * Represents the settings that would otherwise go in xxx-servlet.xml
     */
    @Configuration
    @EnableHypermediaSupport
    //no need for @EnableWebMvc cause all it does is @Import(DelegatingWebMvcConfiguration.class),
    //and here we are extending DelegatingWebMvcConfiguration directly. Extending gives us the option to override.
    //To summarize there are several options here, for more info see http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/web/servlet/config/annotation/EnableWebMvc.html
    //1- only @EnableWebMvc -- applies default spring mvc config
    //2- @EnableWebMvc + extends WebMvcConfigurerAdapter  -- default config + ability to override
    //3- no @EnableWebMvc, but extends DelegatingWebMvcConfiguration (or its superclass WebMvcConfigurationSupport as the docs mention) -- default config + greater ability to override than WebMvcConfigurerAdapter  
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

        //        @Override
        //        protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //            super.addResourceHandlers(registry);
        //
        //            this.configurers.addResourceHandlers(registry);
        //        }
    }
}

