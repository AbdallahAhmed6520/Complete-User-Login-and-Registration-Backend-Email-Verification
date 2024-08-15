package com.example.demo.service;

import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.exception.EmailAlreadyTakenException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.AppUser;
import com.example.demo.model.ConfirmationToken;
import com.example.demo.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public AppUser loadUserByUsername(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public String signUpUser(AppUser appUser) {
        if (appUserRepository.findByEmail(appUser.getEmail()).isPresent()) {
            throw new EmailAlreadyTakenException("Email already taken");
        }
        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        return generateAndSaveToken(appUser);
    }

    public String generateAndSaveToken(AppUser user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }
    public String login(String username, String password) {
        AppUser user = appUserRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("Incorrect password");
        }
        return generateAndSaveToken(user);
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserRequest request, UserDetails userDetails) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You can only update your own data");
        }

        if (appUserRepository.findByEmail(request.getEmail()).isPresent() && !user.getEmail().equals(request.getEmail())) {
            throw new EmailAlreadyTakenException("Email already taken by another user");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        appUserRepository.save(user);
    }

    public void enableAppUser(String email) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setEnabled(true);
        appUserRepository.save(user);
    }
    public void save(AppUser appUser) {
        appUserRepository.save(appUser);
    }
}
