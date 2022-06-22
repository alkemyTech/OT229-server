package com.alkemy.ong.security.configuration;

import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Handles all global security configurations like endpoint access restriction and defining authentication,
 * authorization and encryption policies.
 */
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // Disabling csrf, needed for session cookie authentication, since a token-based authentication protocol
                // is going to be used instead.
                .csrf().disable()
                // Setting the state policy as stateless, disabling the use of persisting sessions for security context.
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Endpoint restriction
                .authorizeRequests(
                        expressionInterceptUrlRegistry -> expressionInterceptUrlRegistry
                                // Allowing requests to the following endpoints to pass through without authentication
                                .antMatchers(
                                        GlobalConstants.Endpoints.REGISTER + "/**",
                                        GlobalConstants.Endpoints.LOGIN + "/**"
                                ).permitAll()
                                .antMatchers(
                                        HttpMethod.GET,
                                        GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO + "/**"
                                ).permitAll()

                                // Requiring authentication and certain roles for any endpoint not specified above
                                .anyRequest().hasAnyAuthority(GlobalConstants.ROLE_USER, GlobalConstants.ROLE_ADMIN)
                );

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
