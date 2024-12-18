package com.example.spring_security_jwt.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         String authorizationHeader = request.getHeader("Authorization");

         if(authorizationHeader != null && !authorizationHeader.isBlank() && authorizationHeader.startsWith("Bearer ")) {
             String jwt = authorizationHeader.substring(7);
             if(jwt.isEmpty() || jwt.isBlank()) {
                 response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
             } else {
                 try {
                     String email = jwtUtil.validateTokenAndRetrieveSubject(jwt);
                     UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
                     UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, userDetails.getPassword(), userDetails.getAuthorities());

                     if(SecurityContextHolder.getContext().getAuthentication() == null) {
                         SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                     }
                 }
                 catch (JWTVerificationException e) {
                     response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
                 }
             }
         }
         filterChain.doFilter(request, response);
    }
}
