package com.backend.aiblog.service;

import com.backend.aiblog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Autowired
    FileStorageService fileStorageService;

    private final String SECRETE_KEY = "m9qA6PunxRWcfQ7OENsIv4KOVenXM6kwDQxHUMMcAdYPYuknf/i9pAdxXv+mTLsC";


    private final String REFRESH_TOKEN_SECRET_KEY = "JmcwT33c9x2GMnb3RA/On836MNXgV5c+DIe8g/zECFY=";

    @Value("${amazonS3.url}")
    private String s3Url;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractRefreshTokenUsername(String token) {
        return extractRefreshTokenClaim(token, Claims::getSubject);
    }

    public <T> T extractRefreshTokenClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllRefreshTokenClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User userDetails) throws IOException {


            Map<String, Object> claims = new HashMap<>();
            claims.put("name", userDetails.getName());
            claims.put("username", userDetails.getUsername());
            claims.put("email", userDetails.getEmail());

            claims.put("profileImage", userDetails.getProfileImage());
            claims.put("coverImage", userDetails.getCoverImage());

            return generateToken(claims, userDetails);
    }

    public String generateRefreshToken(User userDetails) throws IOException {


        Map<String, Object> claims = new HashMap<>();
        claims.put("name", userDetails.getName());
        claims.put("username", userDetails.getUsername());
        claims.put("email", userDetails.getEmail());

        claims.put("profileImage", userDetails.getProfileImage());
        claims.put("coverImage", userDetails.getCoverImage());

        return generateRefreshToken(claims, userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 48))
                .signWith(getRefreshTokenSignInkey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails){
        final String username = extractRefreshTokenUsername(token);

        return (username.equals(userDetails.getUsername())) && !isRefreshTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private boolean isRefreshTokenExpired(String token){
        return extractRefreshTokenExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Date extractRefreshTokenExpiration(String token) {
        return extractRefreshTokenClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims extractAllRefreshTokenClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getRefreshTokenSignInkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInkey() {


        byte[] keyBytes = Decoders.BASE64.decode(SECRETE_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getRefreshTokenSignInkey() {
        byte[] keyBytes = Decoders.BASE64.decode(REFRESH_TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}