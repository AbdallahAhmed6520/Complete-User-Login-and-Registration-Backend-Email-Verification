package com.example.demo.Controller;

import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.service.EmailService;
import com.example.demo.service.PasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/password-reset")
public class PasswordResetController {
    private PasswordResetService passwordResetService;
    private EmailService emailService;

    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        String token = passwordResetService.generateAndSaveTokenResetPassword(request.getEmail());

        String link = "http://localhost:8080/api/v1/password-reset/reset?token=" + token;
        String emailContent = emailService.buildResetPasswordEmail(request.getEmail(), link);
        emailService.send(request.getEmail(), emailContent);

        return ResponseEntity.ok("Password reset link sent to email");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok("Password reset successful");
    }
}
