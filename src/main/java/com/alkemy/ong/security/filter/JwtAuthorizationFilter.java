package com.alkemy.ong.security.filter;

import com.alkemy.ong.security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    public static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtService jwtService;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader(AUTHORIZATION);

        if(jwtService.isBearer(authHeader)){
            try {
                List<GrantedAuthority> roles = jwtService.getRoles(authHeader).stream().
                        map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        jwtService.getUsername(authHeader), null, roles);

                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(request, response);
                SecurityContextHolder.clearContext();
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
                return;
            }
        }else {
            SecurityContextHolder.clearContext();
        }
    }
}
