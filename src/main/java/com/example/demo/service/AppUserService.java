package com.example.demo.service;

import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.exception.CustomException;
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
    public AppUser loadUserByUsername(String email) throws CustomException {
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
        return user.get();
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        if (userExists) {
            throw new CustomException("Email already taken");
        }

        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        return generateAndSaveToken(appUser);
    }

    public String generateAndSaveToken(AppUser user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    public String login(String username, String password) {
        Optional<AppUser> optionalUser = appUserRepository.findByEmail(username);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        AppUser user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("Incorrect password");
        }

        // إنشاء وحفظ التوكن
        return generateAndSaveToken(user);
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserRequest request, UserDetails userDetails) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify if the authenticated user is updating their own data
        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You can only update your own data");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        appUserRepository.save(user);
    }
    public void enableAppUser(String email) {
        appUserRepository.enableAppUser(email);
    }

    public void save(AppUser appUser) {
        appUserRepository.save(appUser);
    }
}
