package emil.find_course.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import emil.find_course.security.jwt.JwtUtils;
import emil.find_course.security.jwt.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Value("${cookie.auth.name}")
    private String cookieName;

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain)
    throws ServletException, IOException {
                System.out.println("In JWT FILTER");
        // Ignore public routes
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/v1/public/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            Cookie[] cookies = request.getCookies();
            String token = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token != null && jwtUtils.validateToken(token)) {
                String email = jwtUtils.getUserEmailFromJwtToken(token);
                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);

                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {

                System.out.println("ELSE IN JWT FILTER");
                throw new JwtException("Invalid or expired token.");
            }
        } catch (ExpiredJwtException e) {
            // e.printStackTrace();
            System.out.println("Expired JWT EXCEPTION");
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
        } catch (JwtException e) {
            System.out.println("JWT EXCEPTION ");

        } catch (Exception e) {
            System.out.println("Exception in JWT FILTER");
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
