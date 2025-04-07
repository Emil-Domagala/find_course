package emil.find_course.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import emil.find_course.domains.entities.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String generateToken(UserDetailsImpl user) {
        String email = user.getUsername();
        String roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
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
                .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
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
            System.out.println("ExpiredJwtException in Jwt Utils");
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
            // throw new JwtAuthException("JWT token has expired!", e);
        } catch (JwtException e) {
            System.out.println("JwtException in Jwt Utils");
            throw new JwtException("JWT token is invalid", e);
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException in Jwt filter");
            throw new IllegalArgumentException("Invalid token format", e);
        } catch (Exception e) {
            System.out.println("RuntimeException in Jwt filter");
            throw new RuntimeException(e);
        }

    }
}
