package com.azeezcodes.attendance_system.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration}") long expirationMs) {
 byte[] keyBytes = Decoders.BASE64.decode(secret);
 this.signingKey = Keys.hmacShaKeyFor(keyBytes);
 this.expirationMs = expirationMs;
}


public String generateToken(UserDetails userDetails) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey)
            .compact();
}

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = extractAllClaims(token);
            String subject = claims.getSubject();
            Date expiration = claims.getExpiration();

            return subject != null
                    && subject.equals(userDetails.getUsername())
                    && expiration != null
                    && expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

   public boolean isTokenExpired(String token) {
       try{
           Claims claims = extractAllClaims(token);
           Date expiration = claims.getExpiration();

           return expiration.before(new Date());

       } catch (JwtException | IllegalArgumentException ex) {
           return false;
       }
    }

}
