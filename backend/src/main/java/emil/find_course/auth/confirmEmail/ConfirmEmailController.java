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

    private final CookieHelper cookieHelper;
    private final JwtUtils jwtUtils;
    private final ConfirmEmailService confirmEmailService;

    @Operation(summary = "Confirm email")
    @PostMapping
    public ResponseEntity<Void> confirmEmail(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Validated @RequestBody RequestConfirmEmailOTT token) {

        final User user = userDetails.getUser();

        confirmEmailService.validateEmail(user, token.getToken());
        String authToken = jwtUtils.generateToken(user);

        ResponseCookie cookie = cookieHelper.setCookie(authCookieName, authToken, authExpiration, "/");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @Operation(summary = "Resend confirm email")
    @PostMapping("/resend")
    public ResponseEntity<Void> resendConfirmEmail(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        confirmEmailService.sendVerificationEmail(userDetails.getUser());

        return ResponseEntity.noContent().build();
    }
}