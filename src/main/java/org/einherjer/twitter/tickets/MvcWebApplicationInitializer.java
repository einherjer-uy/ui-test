package org.einherjer.twitter.tickets;

import java.io.IOException;
import java.util.List;

import org.einherjer.twitter.tickets.controller.TicketController;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.joda.JodaModule;

//AbstractAnnotationConfigDispatcherServletInitializer provides a convenient way to initialize DispatcherServlet (spring mvc)
//it implements WebApplicationInitializer which in a Servlet 3 environment are autodetected and can replace web.xml
//Note that they are not mutually exclusive, the same application can contain one web.xml and several WebApplicationInitializer
//(hence, AbstractAnnotationConfigDispatcherServletInitializer is usually used only for spring mvc settings
//and another WebApplicationInitializer can be used for other servlets, filters or whatever web.xml setting)
//We have a second WebApplicationInitializer in this app (SpringSecurityWebApplicationInitializer) that register the Spring Security filter
//@Order can be used to specify a load order
@Order(1) //see javadoc in AbstractSecurityWebApplicationInitializer
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
        return new String[] { "/tt/*" };
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
    @EnableWebMvc
    //no need for @EnableWebMvc cause all it does is @Import(DelegatingWebMvcConfiguration.class),
    //and here we are extending DelegatingWebMvcConfiguration directly. Extending gives us the option to override.
    //To summarize there are several options here, for more info see http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/web/servlet/config/annotation/EnableWebMvc.html
    //1- only @EnableWebMvc -- applies default spring mvc config
    //2- @EnableWebMvc + extends WebMvcConfigurerAdapter  -- default config + ability to override
    //3- no @EnableWebMvc, but extends DelegatingWebMvcConfiguration (or its superclass WebMvcConfigurationSupport as the docs mention) -- default config + greater ability to override than WebMvcConfigurerAdapter
    //@EnableHypermediaSupport //TODO: HATEOAS
    //@Import(RepositoryRestMvcConfiguration.class) //enables spring-data-rest (commented for the time being cause it handles the GET / (prints list of rest services) and we want it to be the index.html page, and @RestResource on the Repositories doesn't seem to be working)
    @ComponentScan(basePackageClasses = TicketController.class) //include controllers here cause otherwise the resource mappings doesn't work, all other beans are taken from the parent context (see ApplicationConfig)
    public static class WebConfiguration extends WebMvcConfigurerAdapter {

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.defaultContentType(MediaType.APPLICATION_JSON);
        }

        private ObjectMapper objectMapper() {
            Jackson2ObjectMapperFactoryBean bean = new Jackson2ObjectMapperFactoryBean();
            bean.setIndentOutput(true);
            bean.setSimpleDateFormat("MM/dd/yyyy-HH:mm");// ignored according to http://www.lorrin.org/blog/2013/06/28/custom-joda-time-dateformatter-in-jackson/
            bean.afterPropertiesSet();
            ObjectMapper objectMapper = bean.getObject();
            objectMapper.registerModule(new JodaModule()); //JodaModule includes a series of serializers and deserializers but not for this particular "pattern" so we add an additional SimpleModule below
            objectMapper.registerModule(new SimpleModule() {
                {
                    addSerializer(DateTime.class, new StdSerializer<DateTime>(DateTime.class) {
                        @Override
                        public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
                            jgen.writeString(DateTimeFormat.forPattern("MM/dd/yyyy-HH:mm").print(value));
                        }
                    });
                    addDeserializer(DateTime.class, new StdDeserializer<DateTime>(DateTime.class) {
                        @Override
                        public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                            String str = jp.getText().trim();
                            if (str.length() == 0) {
                                return null;
                            }
                            return DateTimeFormat.forPattern("MM/dd/yyyy-HH:mm").parseDateTime(str);
                        }
                    });
                }
            });
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

        //allows for mapping the DispatcherServlet to "/" (thus overriding the mapping of the web server's default Servlet),
        //while still allowing static resource requests to be handled by the web server's default Servlet; but in this case we are mapping to "/tt"
        //        @Override
        //        public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        //            configurer.enable();
        //        }

        //For static resources (html) no need for ViewResolver, just forward and the web server root servlet will respond (see LoginController)
        //        @Bean
        //        public UrlBasedViewResolver viewResolver(){
        //            UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        //            viewResolver.setViewClass(JstlView.class);
        //            viewResolver.setPrefix("/WEB-INF/jsp/");
        //            viewResolver.setSuffix(".jsp");
        //            return viewResolver;
        //        }
                
    }
}

