package com.alkemy.ong.security.configuration;

import com.alkemy.ong.security.filter.JwtAuthorizationFilter;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
                                // Routes to register and login
                                .antMatchers(
                                        GlobalConstants.Endpoints.REGISTER,
                                        GlobalConstants.Endpoints.LOGIN
                                ).permitAll()



                                // Permitted access to a USER
                                .antMatchers(HttpMethod.GET, GlobalConstants.EndpointsRoutes.USER_GET).hasAnyAuthority(GlobalConstants.ALL_ROLES)
                                .antMatchers(HttpMethod.POST, GlobalConstants.EndpointsRoutes.USER_POST).hasAnyAuthority(GlobalConstants.ALL_ROLES)
                                .antMatchers(HttpMethod.PUT, GlobalConstants.EndpointsRoutes.USER_PUT).hasAnyAuthority(GlobalConstants.ALL_ROLES)
                                .antMatchers(HttpMethod.DELETE, GlobalConstants.EndpointsRoutes.USER_DELETE).hasAnyAuthority(GlobalConstants.ALL_ROLES)

                                // ADMIN access -> can do all
                                .antMatchers(HttpMethod.GET, "/**").hasAnyAuthority(GlobalConstants.ROLE_ADMIN)
                                .antMatchers(HttpMethod.POST, "/**").hasAnyAuthority(GlobalConstants.ROLE_ADMIN)
                                .antMatchers(HttpMethod.PUT, "/**").hasAnyAuthority(GlobalConstants.ROLE_ADMIN)
                                .antMatchers(HttpMethod.DELETE, "/**").hasAnyAuthority(GlobalConstants.ROLE_ADMIN)

                                .anyRequest().hasAnyAuthority(GlobalConstants.ROLE_USER, GlobalConstants.ROLE_ADMIN)
                        
                ).addFilter(jwtAuthorizationFilter());



    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception{
        return new JwtAuthorizationFilter(this.authenticationManager());
    }
}
