package com.sharktank.interdepcollab.authentication.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtility {
    /*
     * Hash Generator: https://www.akto.io/tools/hmac-sha-256-hash-generator
     * 
     * Text: "Inter Department Collaboration" Key: "sharktank" Algorithm: SHA-256
     * Hash (in Hex)
     */

    @Value("${com.sharktank.jwt.secret-key}")
    private String SECRET_KEY;

    public String createToken(UserDetails userDetails, Long expireInterval) {
        return this.generateToken(new HashMap<String,String>(), userDetails.getUsername(), expireInterval);
    }

    public String generateToken(Map<String, String> extraClaims, String subject, Long expireInterval) {
        return Jwts.builder().claims(extraClaims).subject(subject).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireInterval)).signWith(getSignKey()).compact();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}