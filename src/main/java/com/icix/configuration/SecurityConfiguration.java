package com.icix.configuration;

//import com.icix.messagebus.api.model.security.StatelessAuthenticationFilter;
//import com.icix.messagebus.api.model.security.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
   // private TokenAuthenticationService tokenAuthenticationService;

    @Override protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
    }
}