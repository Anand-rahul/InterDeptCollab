package com.sharktank.interdepcollab.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sharktank.interdepcollab.authentication.component.JwtFilter;
import com.sharktank.interdepcollab.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;
    private final UserService userService;

    // Security Filter Chain for API
    @Bean
    @Order(1)
    public SecurityFilterChain basicAuthSecurityFilterChain(HttpSecurity http) throws Exception {
        // JWT based Token Authentication is enabled
        // To enable Basic Authentication, comment addFilterBefore(jwtFilter,...) and
        // uncomment httpBasic
        
        return http
                .csrf(csrf -> csrf.disable())
                // .securityMatcher("/api/**")
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/api/open/**","/api/register", "/api/login").permitAll();
                    request.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // .httpBasic(Customizer.withDefaults()) // Basic Authentication (Disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Filter to process JWT
                .build();
    }

    // // Security Filter Chain for Web Pages
    // @Bean
    // @Order(2)
    // public SecurityFilterChain formSecurityFilterChain(HttpSecurity http) throws Exception {
    //     // Form Login for Web Pages
    //     return http.authorizeHttpRequests(request -> {
    //                 request.requestMatchers("/").permitAll();
    //                 request.requestMatchers("/error").permitAll();
    //                 request.requestMatchers("/open").permitAll();
    //                 request.anyRequest().authenticated();
    //             })
    //             .formLogin((formLoginConfig) -> formLoginConfig.defaultSuccessUrl("/protected", true))
    //             .logout(logoutConfig -> logoutConfig.logoutSuccessUrl("/"))
    //             .build();
    // }

    // // Ignore selected URIs from security checks
    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    //     // Ignore static directories from Security Filter Chain
    //     return web -> web.ignoring().requestMatchers("/images/**", "/js/**");
    // }

    // Custom User Details Service to manage login
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetailsService userDetailsService = (userName) -> {
            return userService.findMatch(userName);
        };
        return userDetailsService;
    }

     @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Password encoder
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
