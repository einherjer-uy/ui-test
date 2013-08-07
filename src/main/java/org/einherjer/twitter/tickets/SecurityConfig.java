package org.einherjer.twitter.tickets;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//represents the (usually named) security-context.xml
//@Configuration
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
      auth
        .inMemoryAuthentication()
          .withUser("user")
            .password("password")
            .roles("USER");
    }
   
    @Override
    public void configure(WebSecurity web) throws Exception {
      web
        .ignoring()
           .antMatchers("/resources/**");
    }
   
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
        .authorizeUrls()
//          .antMatchers("/signup","/about").permitAll()
          .anyRequest().authenticated()
          .and()
      .formLogin()
          .loginUrl("/login")
          .permitAll();
    }


}
