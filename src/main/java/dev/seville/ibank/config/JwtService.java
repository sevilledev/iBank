package dev.seville.ibank.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.access-token.secret}")
    private String accessTokenSecret;

    @Value("${jwt.refresh-token.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.access-token.ttl}")
    private long accessTokenTtl;

    @Value("${jwt.refresh-token.ttl}")
    private long refreshTokenTtl;


    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, accessTokenTtl, accessTokenSecret);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshTokenTtl, refreshTokenSecret);
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, accessTokenSecret);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, refreshTokenSecret);
    }

    public String extractUsernameFromAccess(String token) {
        return extractAllClaims(token, accessTokenSecret).getSubject();
    }

    public String extractUsernameFromRefresh(String token) {
        return extractAllClaims(token, refreshTokenSecret).getSubject();
    }


    // Helpers

    private String generateToken(UserDetails userDetails, long ttl, String secretKey) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(getKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenValid(String token, UserDetails userDetails, String secretKey) {
        final String username = extractUsername(token, secretKey);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, secretKey);
    }

    private String extractUsername(String token, String secretKey) {
        return extractAllClaims(token, secretKey).getSubject();
    }

    private boolean isTokenExpired(String token, String secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    private Date extractExpiration(String token, String secretKey) {
        return extractAllClaims(token, secretKey).getExpiration();
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
