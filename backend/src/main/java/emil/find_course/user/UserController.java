package emil.find_course.user;

import org.springframework.http.HttpHeaders;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.auth.dto.response.AuthResponse;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.common.util.CookieHelper;
import emil.find_course.user.becomeTeacher.dto.BecomeTeacherDto;
import emil.find_course.user.becomeTeacher.entity.BecomeTeacher;
import emil.find_course.user.becomeTeacher.mapper.BecomeTeacherMapper;
import emil.find_course.user.dto.UserDto;
import emil.find_course.user.dto.request.RequestUpdateUser;
import emil.find_course.user.entity.User;
import emil.find_course.user.mapper.UserMapper;
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
        @Value("${domain.name}")
        private String domainName;
        @Value("${spring.profiles.active}")
        private String springProfile;

        private final JwtUtils jwtUtils;
        private final UserService userService;
        private final UserMapper userMapper;
        private final BecomeTeacherMapper becomeTeacherMapper;

        @GetMapping
        public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {

                UserDto user = userMapper.toDto(userDetails.getUser());
                return ResponseEntity.ok(user);

        }

        @PatchMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
        public ResponseEntity<UserDto> updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                        @RequestPart("userData") @Validated RequestUpdateUser requestUpdateUser,
                        @RequestPart(required = false) MultipartFile image) {

                User currUser = userDetails.getUser();

                User updatedUser = userService.updateUser(currUser, requestUpdateUser, image);
                UserDto userDto = userMapper.toDto(updatedUser);

                String refreshToken = jwtUtils.generateRefreshToken(updatedUser);
                AuthResponse auth = new AuthResponse(jwtUtils.generateToken(updatedUser), refreshToken);
                ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, auth.token(), cookieExpiration,
                                "/", springProfile, domainName);
                ResponseCookie refreshCookie = CookieHelper.setCookieHelper(
                                refreshCookieName, auth.refreshToken(), refreshCookieExpiration,
                                "/", springProfile, domainName);

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshCookie.toString())
                                .body(userDto);

        }

        @DeleteMapping
        public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
                final User user = userDetails.getUser();
                userService.deleteUser(user);
                ResponseCookie deleteCookie = CookieHelper.setCookieHelper(authCookieName, "", 0, "/", springProfile,
                                domainName);
                ResponseCookie deleteRefreshCookie = CookieHelper.setCookieHelper(refreshCookieName, "", 0,
                                "/", springProfile, domainName);

                return ResponseEntity.noContent()
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString(), deleteRefreshCookie.toString())
                                .build();
        }

        @PostMapping("/become-teacher")
        public ResponseEntity<Void> becomeTeacher(@AuthenticationPrincipal UserDetailsImpl userDetails) {

                final User user = userDetails.getUser();
                userService.createBecomeTeacherRequest(user);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/become-teacher")
        public ResponseEntity<BecomeTeacherDto> getBecomeTeacherRequest(
                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

                final User user = userDetails.getUser();
                Optional<BecomeTeacher> becomeTeacherOpt = userService.getBecomeTeacherRequest(user);
                BecomeTeacherDto becomeTeacherDto = becomeTeacherOpt.map(becomeTeacherMapper::toDto)
                                .orElse(new BecomeTeacherDto());
                return ResponseEntity.ok(becomeTeacherDto);

        }

}
