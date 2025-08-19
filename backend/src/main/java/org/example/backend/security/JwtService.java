package org.example.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {

    private final Key key;
    private final long accessTtlSec;
    private final long refreshTtlSec;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access.ttl-seconds}") long accessTtlSec,
            @Value("${app.jwt.refresh.ttl-seconds}") long refreshTtlSec) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSec = accessTtlSec;
        this.refreshTtlSec = refreshTtlSec;
    }

    public String generateAccessToken(UserDetails user) {
        return buildToken(user.getUsername(), accessTtlSec, Map.of("roles", user.getAuthorities()));
    }

    public String generateRefreshToken(UserDetails user) {
        return buildToken(user.getUsername(), refreshTtlSec, Map.of("type", "refresh"));
    }

    private String buildToken(String subject, long ttlSec, Map<String, ?> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        try {
            var body = parse(token).getBody();
            return user.getUsername().equals(body.getSubject())
                    && body.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
