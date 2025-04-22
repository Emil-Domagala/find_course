package emil.find_course.controllers;

import org.springframework.http.HttpHeaders;

import java.security.Principal;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.domains.dto.AuthResponse;
import emil.find_course.domains.dto.BecomeTeacherDto;
import emil.find_course.domains.dto.UserDto;
import emil.find_course.domains.entities.BecomeTeacher;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.RequestUpdateUser;
import emil.find_course.mapping.BecomeTeacherMapping;
import emil.find_course.mapping.UserMapping;
import emil.find_course.security.jwt.JwtUtils;
import emil.find_course.services.AuthService;
import emil.find_course.services.UserService;
import emil.find_course.utils.CookieHelper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Value("${jwt.authToken.expiration}")
    private int cookieExpiration;
    @Value("${cookie.auth.refreshToken.name}")
    private String refreshCookieName;
    @Value("${jwt.refreshToken.expiration}")
    private int refreshCookieExpiration;

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final UserMapping userMapping;
    private final BecomeTeacherMapping becomeTeacherMapping;

    @GetMapping
    public ResponseEntity<UserDto> getUserInfo(Principal principal) {

        UserDto user = userMapping.toDto(userService.findByEmail(principal.getName()));
        return ResponseEntity.ok(user);

    }

    @PatchMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<UserDto> updateUserInfo(Principal principal,
            @RequestPart("userData") @Validated RequestUpdateUser requestUpdateUser,
            @RequestPart(required = false) MultipartFile image) {

        User currUser = userService.findByEmail(principal.getName());

        User updatedUser = userService.updateUser(currUser, requestUpdateUser, image);
        UserDto userDto = userMapping.toDto(updatedUser);

        String refreshToken = jwtUtils.generateRefreshToken(updatedUser);
        AuthResponse auth = new AuthResponse(jwtUtils.generateToken(updatedUser), refreshToken);
        ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, auth.token(), cookieExpiration,
                "/");
        ResponseCookie refreshCookie = CookieHelper.setCookieHelper(
                refreshCookieName, auth.refreshToken(), refreshCookieExpiration,
                "/");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshCookie.toString())
                .body(userDto);

    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        userService.deleteUser(user);
        ResponseCookie deleteCookie = CookieHelper.setCookieHelper(authCookieName, "", 0, "/");
        ResponseCookie deleteRefreshCookie = CookieHelper.setCookieHelper(refreshCookieName, "", 0,
                "/");

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString(), deleteRefreshCookie.toString())
                .build();
    }

    @PostMapping("/become-teacher")
    public ResponseEntity<Void> becomeTeacher(Principal principal) {

        User user = userService.findByEmail(principal.getName());
        userService.createBecomeTeacherRequest(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/become-teacher")
    public ResponseEntity<BecomeTeacherDto> getBecomeTeacherRequest(Principal principal) {

        User user = userService.findByEmail(principal.getName());
        Optional<BecomeTeacher> becomeTeacherOpt = userService.getBecomeTeacherRequest(user);
        BecomeTeacherDto becomeTeacherDto = becomeTeacherOpt.map(becomeTeacherMapping::toDto)
                .orElse(new BecomeTeacherDto());
        return ResponseEntity.ok(becomeTeacherDto);

    }

}
