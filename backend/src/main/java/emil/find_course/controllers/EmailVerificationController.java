package emil.find_course.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.RequestConfirmEmailOTT;
import emil.find_course.security.jwt.JwtUtils;
import emil.find_course.services.EmailVerificationService;
import emil.find_course.services.UserService;
import emil.find_course.utils.CookieHelper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EmailVerificationController {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Value("${jwt.authToken.expiration}")
    private int cookieExpiration;
    @Value("${frontend.domain}")
    private String frontendDomain;

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(Principal principal,
            @Validated @RequestBody RequestConfirmEmailOTT token) {

        User user = userService.findByEmail(principal.getName());

        emailVerificationService.validateEmail(user, token.getToken());
        String authToken = jwtUtils.generateToken(user);

        ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, authToken, cookieExpiration, "/");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(authToken);
    }

    @PostMapping("/confirm-email/resend")
    public ResponseEntity<Void> resendConfirmEmail(Principal principal) {
        emailVerificationService.sendVerificationEmail(userService.findByEmail(principal.getName()));

        return ResponseEntity.noContent().build();
    }
}