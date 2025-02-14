package com.sharktank.interdepcollab.authentication.component;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.sharktank.interdepcollab.authentication.service.JwtUtility;
import com.sharktank.interdepcollab.user.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;
    private final UserService userService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            processToken(request);
        } catch (Exception e) {
            log.error(String.format("Failed to process JWT Token: %s", e.getMessage()));
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

        log.debug("Processing complete. Return back control to framework");
        filterChain.doFilter(request, response);
    }

    private void processToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        logger.info(String.format("Authorization Header: %s", authHeader));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("No Bearer Header, skip processing");
            return;
        }

        final String jwtToken = authHeader.substring(7);

        if (jwtUtility.isTokenExpired(jwtToken)) {
            logger.info("Token validity expired");
            return;
        }

        String userName = jwtUtility.extractUsername(jwtToken);
        if (userName == null) {
            logger.info("No username found in JWT Token");
            return;
        }

        logger.info("Username found in JWT: " + userName);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            logger.info("Already logged in: " + userName);
            return;
        }

        logger.info(String.format("Create authentication instance for %s", userName));
        UserDetails userDetails = userService.findMatch(userName);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
