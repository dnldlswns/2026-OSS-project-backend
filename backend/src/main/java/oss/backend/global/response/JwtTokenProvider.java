package oss.backend.global.response;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import oss.backend.domain.user.entity.User;

@Component
public class JwtTokenProvider {
        private final SecretKey key;
        private final long accessTokenExpirationMs;

        public JwtTokenProvider(
                        @Value("${jwt.secret}") String secret,
                        @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
                this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
                this.accessTokenExpirationMs = accessTokenExpirationMs;
        }

        public String createAccessToken(User user) {
                Instant now = Instant.now();

                return Jwts.builder()
                                .subject(user.getEmail())
                                .claim("userPk", user.getId())
                                .claim("email", user.getEmail())
                                .claim("role", user.getRole().name())
                                .issuedAt(Date.from(now))
                                .expiration(Date.from(now.plusMillis(accessTokenExpirationMs)))
                                .signWith(key, Jwts.SIG.HS256)
                                .compact();
        }

        public boolean validateToken(String token) {
                try {
                        Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                        return true;
                } catch (JwtException | IllegalArgumentException e) {
                        return false;
                }
        }

        public String getEmail(String token) {
                Claims claims = Jwts.parser().verifyWith(key).build()
                                .parseSignedClaims(token).getPayload();
                return claims.getSubject();
        }

        public String getRole(String token) {
                Claims claims = Jwts.parser().verifyWith(key).build()
                                .parseSignedClaims(token).getPayload();
                return claims.get("role", String.class);
        }
}
