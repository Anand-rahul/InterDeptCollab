package com.sharktank.interdepcollab.authentication.controller;

import java.util.Dictionary;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharktank.interdepcollab.authentication.model.AuthenticationRequest;
import com.sharktank.interdepcollab.authentication.service.JwtUtility;
import com.sharktank.interdepcollab.exception.UserExistsException;
import com.sharktank.interdepcollab.user.model.AppUser;
import com.sharktank.interdepcollab.user.service.UserService;

import io.micrometer.core.ipc.http.HttpSender.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtil;

    @Value("${com.sharktank.jwt.expiration-time}")
    private Long JWT_EXPIRATION;

    // BUG: When request is sent with same employeeId, user is replaced
     @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AppUser user) throws UserExistsException {
        // Encode the user's password
        log.info("Registering user: " + user);
        log.info("Raw Password: " + user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save the user to the database
        log.info("Encoded Password: " + user.getPassword());
        userService.addUser(user);
        return new ResponseEntity<String>("User registered successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.createToken(userDetails, JWT_EXPIRATION);

        return new ResponseEntity<String>(jwt, HttpStatus.OK);
    }

    @GetMapping("/user/get-all")
    public ResponseEntity<Map<String, String>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
