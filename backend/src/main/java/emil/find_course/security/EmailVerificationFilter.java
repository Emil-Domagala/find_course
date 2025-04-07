package emil.find_course.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.domains.dto.ApiErrorResponse;
import emil.find_course.security.jwt.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class EmailVerificationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("In EmailVerificationFilter");

        // Skip public and verify email
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/v1/public/") || requestURI.contains("confirm-email")) {
            filterChain.doFilter(request, response);
            return;
        }
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        if (!userDetailsImpl.isEmailVerified()) {
            System.out.println("EMAIL NOT VERIFIED");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .message("Email is not verified. Please verify your email to proceed.")
                    .build();

            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(errorResponse));
            writer.flush();
            return;
        }

        filterChain.doFilter(request, response);
    }

}
