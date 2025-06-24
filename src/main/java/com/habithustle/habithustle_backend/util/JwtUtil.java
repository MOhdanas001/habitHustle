package com.habithustle.habithustle_backend.util;

import com.habithustle.habithustle_backend.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {

   @Value("${jwt.secret}")
   private String secretKey;

   private Key getSignInKey() {
      return Keys.hmacShaKeyFor(secretKey.getBytes());
   }

   public String extractUsername(String token) {
      return extractClaim(token, Claims::getSubject);
   }

   public <T> T extractClaim(String token, Function<Claims, T> resolver) {
      final Claims claims = Jwts.parser()
              .setSigningKey(getSignInKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
      return resolver.apply(claims);
   }

   public String generateToken(User userDetails) {
      return Jwts.builder()
              .setSubject(userDetails.getEmail())
              .setIssuedAt(new Date())
              .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
              .signWith(getSignInKey(), SignatureAlgorithm.HS256)
              .compact();
   }

   public boolean isTokenValid(String token, UserDetails userDetails) {
      final String username = extractUsername(token);
      return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
   }

   private boolean isTokenExpired(String token) {
      return extractClaim(token, Claims::getExpiration).before(new Date());
   }
}
