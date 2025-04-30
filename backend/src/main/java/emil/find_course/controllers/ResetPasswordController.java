package emil.find_course.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.requestDto.EmailRequest;
import emil.find_course.domains.requestDto.PasswordRequest;
import emil.find_course.services.ResetPasswordService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @PostMapping("/public/forgot-password")
    public ResponseEntity<Void> sendResetPasswordEmail(@Validated @RequestBody EmailRequest email) {
        resetPasswordService.sendResetPasswordEmail(email.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam(required = false) String token,
            @Validated @RequestBody PasswordRequest password) {

        resetPasswordService.resetPassword(token, password.getPassword());
        return ResponseEntity.noContent().build();
    }

}
