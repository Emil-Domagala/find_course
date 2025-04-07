package emil.find_course.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import emil.find_course.domains.entities.user.User;
import emil.find_course.exceptions.JwtAuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.authToken.expiration}")
    private int jwtAuthTokenExpirationMs;

    public String generateToken(UserDetailsImpl user) {
        String email = user.getUsername();
        String roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtAuthTokenExpirationMs)))
                .signWith(key())
                .compact();
    }

    public String generateToken(User user) {
        String email = user.getEmail();
        String roles = user.getRoles().stream().map(role -> "ROLE_" + role.name()).collect(Collectors.joining(","));
        boolean isVerified = user.isEmailVerified();

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .claim("isEmailVerified",
                        isVerified)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtAuthTokenExpirationMs)))
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserEmailFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key())
                    .build().parseSignedClaims(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtAuthException("JWT token has expired!", e);
        } catch (JwtException e) {
            throw new JwtAuthException("JWT token exception!", e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected problem occured in Jwt filter", e);
        }

    }
}
