package org.einherjer.twitter.tickets;

import java.util.List;

import org.einherjer.twitter.tickets.controller.TicketController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

//AbstractAnnotationConfigDispatcherServletInitializer provides a convenient way to initialize DispatcherServlet (spring mvc)
//it implements WebApplicationInitializer which in a Servlet 3 environment are autodetected and can replace web.xml
//Note that they are not mutually exclusive, the same application can contain one web.xml and several WebApplicationInitializer
//We have a second WebApplicationInitializer in this app (SpringSecurityWebApplicationInitializer) that register the Spring Security filter
//@Order can be used to specify a load order 
public class MvcWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { ApplicationConfig.class, SecurityConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebConfiguration.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/api/*" };
    }

    //Needed because after the @Service returns the @Transactional method ends and the @Entity gets detached,
    //but after that Spring MVC, etc needs to access the entity to serialize it to json
    @Override
    protected javax.servlet.Filter[] getServletFilters() {
        return new javax.servlet.Filter[] { new OpenEntityManagerInViewFilter() };
    }

    /*
     * <!-- Ensure UTF-8 encoded pages so that certain characters are displayed and submitted correctly -->
    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
     */

    /**
    * Web layer configuration enabling Spring MVC, Spring Hateoas hypermedia support.
    */
    //Represents the settings that would otherwise go in xxx-servlet.xml
    @Configuration
    //no need for @EnableWebMvc cause all it does is @Import(DelegatingWebMvcConfiguration.class),
    //and here we are extending DelegatingWebMvcConfiguration directly. Extending gives us the option to override.
    //To summarize there are several options here, for more info see http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/web/servlet/config/annotation/EnableWebMvc.html
    //1- only @EnableWebMvc -- applies default spring mvc config
    //2- @EnableWebMvc + extends WebMvcConfigurerAdapter  -- default config + ability to override
    //3- no @EnableWebMvc, but extends DelegatingWebMvcConfiguration (or its superclass WebMvcConfigurationSupport as the docs mention) -- default config + greater ability to override than WebMvcConfigurerAdapter
    @EnableHypermediaSupport
    @Import(RepositoryRestMvcConfiguration.class) //enables spring-data-rest
    @ComponentScan(basePackageClasses = TicketController.class) //include controllers here cause otherwise the resource mappings doesn't work, all other beans are taken from the parent context (see ApplicationConfig)
    public static class WebConfiguration extends DelegatingWebMvcConfiguration {

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

