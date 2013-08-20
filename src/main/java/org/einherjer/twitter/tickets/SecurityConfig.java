package org.einherjer.twitter.tickets;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//represents the (usually named) security-context.xml
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("user@twitter.com")
                    .password("Admin_123")
                    .roles("REQUESTOR")
                    .and()
                .withUser("req@twitter.com")
                    .password("Admin_123")
                    .roles("REQUESTOR")
                    .and()
                .withUser("app@twitter.com")
                    .password("Admin_123")
                    .roles("APPROVER")
                    .and()
                .withUser("exe@twitter.com")
                    .password("Admin_123")
                    .roles("EXECUTOR");
    }
   
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers("/public/**");
    }
   
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeUrls()
                .antMatchers("/favicon.ico", "/robots.txt").permitAll()
                .anyRequest().authenticated() //always catch any other request at the end, and request authentication by default 
                .and()
            .formLogin()
                .loginPage("/content/login.html") //loginUrl is the same as loginPage but it also sets the loginProcessingUrl to the same value. loginUrl and loginProcessingUrl can have the same value (the difference would be that, in a restful fashion, one will process GET and the other POST)
                .loginProcessingUrl("/login") //must match the "action" attribute of the <form> in the login page, it can be anything, no need to have a @Controller for that url, Spring Security handles the request 
                .failureUrl("/content/login.html?error") //always defaults to login?error if not specified
                //we can specify default-target-url="/home.html", and always-use-default-target="true" if we want the successful login to redirect always to the home page (by default it will redirect to the url that the user requested before being redirected to the login)
            .permitAll()
            .and()
            .logout()
//                .logoutUrl("/tt/login?logout") //use default /logout
                .logoutSuccessUrl("/content/login.html") //use default /login?logout
            .permitAll();
    }

}
