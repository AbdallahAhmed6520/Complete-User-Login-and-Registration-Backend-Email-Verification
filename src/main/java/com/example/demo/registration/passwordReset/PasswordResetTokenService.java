package com.example.demo.registration.passwordReset;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public void savePasswordResetToken(PasswordResetToken token) {
        passwordResetTokenRepository.save(token);
    }

    public Optional<PasswordResetToken> getToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }
    public void setConfirmedAt(String token) {
        passwordResetTokenRepository.findByToken(token)
                .ifPresent(t -> {
                    t.setConfirmedAt(LocalDateTime.now());
                    passwordResetTokenRepository.save(t);
                });
    }

}
