package emil.find_course.user;

import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.common.util.CookieHelper;
import emil.find_course.common.util.CookieHelper.AllAuthCookies;
import emil.find_course.user.dto.UserDto;
import emil.find_course.user.dto.request.RequestUpdateUser;
import emil.find_course.user.entity.User;
import emil.find_course.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Controller", description = "Endpoints for user")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

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
        private final UserService userService;
        private final UserMapper userMapper;

        @Operation(summary = "Get user info")
        @GetMapping
        public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {

                UserDto user = userMapper.toDto(userDetails.getUser());
                return ResponseEntity.ok(user);

        }

        // PICTURE IS BEING DELETED WHEN IT SHOULDNT
        @Operation(summary = "Update user profile information")
        @PatchMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
        public ResponseEntity<UserDto> updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                        @RequestPart("userData") @Validated RequestUpdateUser requestUpdateUser,
                        @RequestPart(required = false) MultipartFile image) {

                User currUser = userDetails.getUser();

                User updatedUser = userService.updateUser(currUser, requestUpdateUser, image);
                UserDto userDto = userMapper.toDto(updatedUser);

                AllAuthCookies authCookies = cookieHelper.createAllAuthCookies(updatedUser);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, authCookies.getAuthCookie().toString())
                                .header(HttpHeaders.SET_COOKIE, authCookies.getAccessCookie().toString())
                                .header(HttpHeaders.SET_COOKIE, authCookies.getRefreshCookie().toString())
                                .body(userDto);

        }

        @Operation(summary = "Delete user")
        @DeleteMapping
        public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
                final User user = userDetails.getUser();
                userService.deleteUser(user);
                ResponseCookie deleteCookie = cookieHelper.setCookie(authCookieName, "", 0, "/");
                ResponseCookie deleteRefreshCookie = cookieHelper.setCookie(refreshCookieName, "", 0,
                                "/api/v1/public/refresh-token");
                ResponseCookie deleteAccessCookie = cookieHelper.setCookie(accessCookieName, "", 0, "/");

                return ResponseEntity.noContent()
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString())
                                .build();
        }

}
