package emil.find_course.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import emil.find_course.domains.entities.user.User;
import emil.find_course.exceptions.JwtAuthException;
import emil.find_course.exceptions.UnauthorizedException;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    Dotenv dotenv = Dotenv.load();

    private final String jwtSecret = dotenv.get("JWT_SECRET");

    @Value("${jwt.authToken.expiration}")
    private int jwtAuthTokenExpirationMs;

    @Value("${jwt.refreshToken.expiration}")
    private int refreshTokenExpirationMs;

    public String generateRefreshToken(User user) {
        String email = user.getEmail();
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + refreshTokenExpirationMs)))
                .signWith(key())
                .compact();
    }

    public String generateToken(UserDetailsImpl user) {
        System.out.println(jwtSecret);
        String email = user.getUsername();
        String roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(","));
        String imageUrl = user.getUser().getImageUrl();
        boolean isVerified = user.isEmailVerified();

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .claim("isEmailVerified",
                        isVerified)
                .claim("picture",
                        imageUrl)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtAuthTokenExpirationMs)))
                .signWith(key())
                .compact();
    }

    public String generateToken(User user) {
        System.out.println(jwtSecret);
        String email = user.getEmail();
        String roles = user.getRoles().stream().map(role -> "ROLE_" + role.name()).collect(Collectors.joining(","));
        boolean isVerified = user.isEmailVerified();
        String imageUrl = user.getImageUrl();

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .claim("isEmailVerified",
                        isVerified)
                .claim("picture",
                        imageUrl)
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
            System.out.println("validate auth token");
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

    public boolean validateRefreshToken(String refreshToken) {
        try {
            System.out.println("validate refresh token");
            System.out.println(refreshToken);
            System.out.println("?//////////////////");
            Jwts.parser().verifyWith((SecretKey) key())
                    .build().parseSignedClaims(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Refresh token expired" + e);
            throw new UnauthorizedException("Refresh token expired");
        } catch (Exception e) {
            System.out.println("Invalid refresh token" + e);
            throw new UnauthorizedException("Invalid refresh token", e);
        }
    }
}
