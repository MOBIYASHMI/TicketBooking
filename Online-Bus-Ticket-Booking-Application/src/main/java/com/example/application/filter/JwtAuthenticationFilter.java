package com.example.application.filter;


import com.example.application.util.JwtUtil;
import com.example.application.util.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        final String authorizationHeader=request.getHeader("Authorization");
//
//        String jwt=null;
//        String username=null;
//
//        System.out.println("Checking request: " + request.getRequestURI());
//        System.out.println("Received Authorization Header: "+ authorizationHeader);
//
//        if (request.getRequestURI().startsWith("/auth/")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")){
//            jwt=authorizationHeader.substring(7);
//            username=jwtUtil.extractUsername(jwt);
//            System.out.println("Extracted username : "+username);
//        }else {
//            System.out.println("Authorization header is missing or invalid");
//        }
//
//        if(username !=null && SecurityContextHolder.getContext().getAuthentication()==null){
//            UserDetails userDetails=userDetailsService.loadUserByUsername(username);
//            if (jwtUtil.validateToken(jwt,userDetails)){
//                UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                System.out.println("JWT authentication set for: "+username);
//            }
//        }
//        filterChain.doFilter(request,response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = null;
        String username = null;

        System.out.println("Checking request: " + request.getRequestURI());

        // Extract JWT from Cookie
        if (request.getCookies() != null) {
            Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> "jwtToken".equals(cookie.getName()))
                    .findFirst();

            if (jwtCookie.isPresent()) {
                jwt = jwtCookie.get().getValue();
                System.out.println("Extracted JWT from cookie: " + jwt);
            } else {
                System.out.println("jwtToken cookie not found");
            }
        } else {
            System.out.println("No cookies found in the request");
        }

        if (jwt != null) {
            username = jwtUtil.extractUsername(jwt);
            System.out.println("Extracted username: " + username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("JWT authentication set for: " + username);
            }
        }
        filterChain.doFilter(request, response);
    }
}
