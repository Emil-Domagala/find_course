package emil.find_course.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import emil.find_course.exceptions.EmailConfirmException;
import emil.find_course.security.jwt.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailVerificationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip public and verify email
        try {
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/api/v1/public/") || requestURI.contains("confirm-email")) {
                filterChain.doFilter(request, response);
                return;
            }
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();

            if (!userDetailsImpl.isEmailVerified()) {
                System.out.println("EMAIL NOT VERIFIED");
                throw new EmailConfirmException("Email is not verified. Please verify your email to proceed.");
            }
        } catch (EmailConfirmException e) {
            exceptionResolver.resolveException(request, response, null, e);
            return;
        } catch (Exception e) {
            System.out.println("unknown Exception in Email Verification FILTER");
            exceptionResolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
