package emil.find_course.auth.dto.response;

public record AuthResponse(String token, String refreshToken, String accessToken) {
}
