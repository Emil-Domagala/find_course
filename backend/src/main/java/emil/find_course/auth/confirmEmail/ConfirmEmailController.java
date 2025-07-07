package emil.find_course.auth.confirmEmail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.auth.confirmEmail.dto.request.RequestConfirmEmailOTT;
import emil.find_course.auth.dto.response.AuthResponse;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.common.util.CookieHelper;
import emil.find_course.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Confirm Email Controller", description = "Endpoints for confirm email")
@RestController
@RequestMapping("/api/v1/confirm-email")
@RequiredArgsConstructor
public class ConfirmEmailController {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    @Value("${jwt.authToken.expiration}")
    private int authExpiration;

    @Value("${cookie.auth.refreshToken.name}")
    private String refreshCookieName;
    @Value("${jwt.refreshToken.expiration}")
    private int refreshCookieExpiration;

    @Value("${cookie.auth.accessCookie.name}")
    private String accessCookieName;

    private final CookieHelper cookieHelper;
    private final JwtUtils jwtUtils;
    private final ConfirmEmailService confirmEmailService;

    @Operation(summary = "Confirm email")
    @PostMapping
    public ResponseEntity<AuthResponse> confirmEmail(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Validated @RequestBody RequestConfirmEmailOTT token) {

        final User user = userDetails.getUser();

        confirmEmailService.validateEmail(user, token.getToken());

        AuthResponse auth = new AuthResponse(jwtUtils.generateToken(user), jwtUtils.generateRefreshToken(user),jwtUtils.generateToken(user));
        ResponseCookie authTokenCookie = cookieHelper.setCookie(authCookieName, auth.token(), authExpiration,
                "/");
        ResponseCookie refreshCookie = cookieHelper.setCookie(
                refreshCookieName, auth.refreshToken(),
                refreshCookieExpiration,
                "/api/v1/public/refresh-token");

        ResponseCookie accessCookie = cookieHelper.setCookie(accessCookieName, auth.accessToken(), refreshCookieExpiration,
                "/");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(auth);
    }

    @Operation(summary = "Resend confirm email")
    @PostMapping("/resend")
    public ResponseEntity<Void> resendConfirmEmail(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        confirmEmailService.sendVerificationEmail(userDetails.getUser());

        return ResponseEntity.noContent().build();
    }
}