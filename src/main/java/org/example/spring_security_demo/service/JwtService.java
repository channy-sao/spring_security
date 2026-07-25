package org.example.spring_security_demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtService {
  @Value("${app.jwt.secret-key}")
  private String secreteKey;

  @Value("${app.jwt.access-token-expiration}")
  private Long accessTokenExpiration;

  public String generateAccessToken(String username) {
    Date now = new Date();
    return Jwts.builder()
        .issuedAt(now)
        .subject(username)
        .expiration(new Date(now.getTime() + accessTokenExpiration))
        .signWith(getSignInKey())
        .issuer("jwt.com")
        .compact();
  }

  public Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  public String extractUserName(String token) {
    return extractClaims(token).getSubject();
  }

  public Date extractExpiration(String token) {
    return extractClaims(token).getExpiration();
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public boolean isValidToken(String token) {
    try {
      return !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException ex) {
      log.error(ex.getMessage());
      return false;
    }
  }

  /** Get signing key from secret */
  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secreteKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
