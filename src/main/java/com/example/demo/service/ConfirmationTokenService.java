package com.example.demo.service;

import com.example.demo.model.AppUser;
import com.example.demo.model.ConfirmationToken;
import com.example.demo.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        confirmationTokenRepository.findByToken(token)
                .ifPresent(t -> {
                    t.setConfirmedAt(LocalDateTime.now());
                    confirmationTokenRepository.save(t);
                });
    }

    public Optional<ConfirmationToken> getTokenByUser(AppUser user) {
        return confirmationTokenRepository.findByAppUser(user);
    }
}
