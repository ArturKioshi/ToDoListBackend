package com.kioshi.todolist.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter{

    @Autowired
    private JWTProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        SecurityContextHolder.getContext().setAuthentication(null);

        String header = request.getHeader("Authorization");

        try{
            if(header != null){
            DecodedJWT decodedJWT = this.jwtProvider.validateToken(header);

            String subjectToken = decodedJWT.getSubject();
            String role = decodedJWT.getClaim("role").asString();

            if(subjectToken.isEmpty() || role.isEmpty()){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            request.setAttribute("user_id", subjectToken);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(subjectToken, null, List.of(authority));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        }catch(Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        filterChain.doFilter(request, response);

    }
    
}
