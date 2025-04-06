package emil.find_course.security;

import java.io.IOException;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.domains.dto.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException)
            throws IOException, ServletException {

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .message(authException.getMessage())
                .build();
        System.out.println(errorResponse.getMessage());
        System.out.println(authException);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}