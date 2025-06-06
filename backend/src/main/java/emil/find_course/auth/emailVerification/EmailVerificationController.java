package emil.find_course.auth.emailVerification;

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

import emil.find_course.auth.emailVerification.dto.request.RequestConfirmEmailOTT;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.common.util.CookieHelper;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EmailVerificationController {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Value("${jwt.authToken.expiration}")
    private int authExpiration;

    private final CookieHelper cookieHelper;
    private final JwtUtils jwtUtils;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/confirm-email")
    public ResponseEntity<Void> confirmEmail(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Validated @RequestBody RequestConfirmEmailOTT token) {

        final User user = userDetails.getUser();

        emailVerificationService.validateEmail(user, token.getToken());
        String authToken = jwtUtils.generateToken(user);

        ResponseCookie cookie = cookieHelper.setCookie(authCookieName, authToken, authExpiration, "/");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @PostMapping("/confirm-email/resend")
    public ResponseEntity<Void> resendConfirmEmail(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        emailVerificationService.sendVerificationEmail(userDetails.getUser());

        return ResponseEntity.noContent().build();
    }
}