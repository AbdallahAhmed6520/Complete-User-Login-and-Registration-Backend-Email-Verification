package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRole;
import com.example.demo.appuser.AppUserService;
import com.example.demo.email.EmailSender;
import com.example.demo.exception.InvalidEmailException;
import com.example.demo.exception.TokenAlreadyConfirmedException;
import com.example.demo.exception.TokenExpiredException;
import com.example.demo.exception.TokenNotFoundException;
import com.example.demo.registration.token.ConfirmationToken;
import com.example.demo.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);


    @Transactional
    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.
                test(request.getEmail());

        if (!isValidEmail) {
            throw new InvalidEmailException("Invalid email address");
        }

        String token = appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );

        String link = "http://localhost:8080/api/v1/registration/confirm?token=" + token;
        emailSender.send(request.getEmail(),
                buildEmail(request.getFirstName(), link));

        return token;
    }

    @Transactional
    public ResponseEntity<String> confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new TokenNotFoundException("Token not found"));


        if (confirmationToken.getConfirmedAt() != null) {
            throw new TokenAlreadyConfirmedException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return ResponseEntity.ok("Email confirmed successfully");
    }

    public String resendToken(String email) {
        logger.info("Attempting to resend token for email: {}", email);

        Optional<AppUser> optionalUser = appUserService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("Email not found");
        }
        AppUser user = optionalUser.get();
        logger.info("User found: {}", user.getFirstName());

        Optional<ConfirmationToken> existingToken = confirmationTokenService.getTokenByUser(user);
        if (existingToken.isPresent()) {
            ConfirmationToken token = existingToken.get();
            logger.info("Existing token found: {}", token.getToken());
            if (!token.isExpired()) {
                logger.info("Token is still valid");
                return "Token is still valid";
            } else {
                logger.info("Token is expired");
            }
        } else {
            logger.info("No existing token found");
        }

        String newToken = UUID.randomUUID().toString();
        ConfirmationToken token = new ConfirmationToken(newToken, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);
        confirmationTokenService.saveConfirmationToken(token);
        logger.info("New token generated: {}", newToken);

        String link = "http://localhost:8080/api/v1/registration/confirm?token=" + newToken;
        emailSender.send(
                email,
                buildEmail(user.getFirstName(), link));

        logger.info("New token sent to email: {}", email);
        return "New token sent";
    }

    private String buildEmail(String name, String link) {
        try {
            // Read HTML template from resources
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("templates/email-template.html");

            if (inputStream == null) {
                throw new IllegalStateException("Failed to find email template");
            }

            String emailTemplate;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                emailTemplate = reader.lines().collect(Collectors.joining("\n"));
            }

            // Replace placeholders with actual values
            emailTemplate = emailTemplate.replace("{name}", name)
                    .replace("{link}", link);

            return emailTemplate;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception (e.g., log error or return a default message)
            return "Failed to load email template";
        }
    }
}
