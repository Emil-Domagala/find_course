package emil.find_course.common.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import emil.find_course.common.exception.JwtAuthException;
import emil.find_course.common.exception.JwtTokenRequiredException;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Ignore public routes
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/v1/public/") || requestURI.contains("/uploads/images/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Cookie[] cookies = request.getCookies();
            String token = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(authCookieName)) {
                        token = cookie.getValue();
                        break;
                    }
                }
            } else {
                throw new JwtTokenRequiredException("No cookie provided");
            }

            if (token == null) {
                throw new JwtTokenRequiredException("No token provided");
            }
            jwtUtils.validateToken(token); // throws exception

            String email = jwtUtils.getUserEmailFromJwtToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (JwtAuthException e) {
            exceptionResolver.resolveException(request, response, null, e);
            return;
        } catch (Exception e) {
            exceptionResolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
