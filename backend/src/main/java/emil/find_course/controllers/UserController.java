package emil.find_course.controllers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.UserDto;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.RequestUpdateUser;
import emil.find_course.mapping.UserMapping;
import emil.find_course.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapping userMapping;

    @GetMapping
    public ResponseEntity<?> getUserInfo(Principal principal) {

        UserDto user = userMapping.toDto(userService.findByEmail(principal.getName()));
        return ResponseEntity.ok(user);

    }

    @PatchMapping
    public ResponseEntity<?> updateUserInfo(Principal principal,
            @Validated @RequestBody RequestUpdateUser requestUpdateUser) {

        User user = userService.findByEmail(principal.getName());

        UserDto newUser = userMapping.toDto(userService.updateUser(requestUpdateUser, user));

        return ResponseEntity.ok(newUser);

    }

}
