package com.example.demo.registration.passwordReset;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserService;
import com.example.demo.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    public String generateAndSaveToken(String email) {
        Optional<AppUser> optionalUser = appUserService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        AppUser user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, LocalDateTime.now().plusHours(1), user);
        passwordResetTokenRepository.save(passwordResetToken);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);
        if (optionalToken.isEmpty() || optionalToken.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token is invalid or expired");
        }

        PasswordResetToken passwordResetToken = optionalToken.get();
        AppUser user = passwordResetToken.getAppUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        appUserService.save(user);

        // Optionally set the token as confirmed or used
        passwordResetTokenRepository.delete(passwordResetToken);  // Optionally delete the token after use
    }
}
