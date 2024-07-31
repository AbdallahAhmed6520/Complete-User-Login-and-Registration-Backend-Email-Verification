package com.example.demo.Controller;

import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.email.EmailSender;
import com.example.demo.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailSender emailService;

    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        String token = passwordResetService.generateAndSaveToken(request.getEmail());

        String link = "http://localhost:8080/api/v1/password-reset/reset?token=" + token;
        String emailContent = buildResetPasswordEmail(request.getEmail(), link);
        emailService.send(request.getEmail(), emailContent);

        return ResponseEntity.ok("Password reset link sent to email");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok("Password reset successful");
    }

    private String buildResetPasswordEmail(String email, String link) {
        return "Hi,\n\n" +
                "To reset your password, please click the following link:\n" +
                link + "\n\n" +
                "If you did not request this, please ignore this email.";
    }
}
