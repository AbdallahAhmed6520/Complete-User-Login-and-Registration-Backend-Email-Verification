package com.example.demo.auth;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder PasswordEncoder;

    @Autowired
    public UserService(AppUserRepository appUserRepository, PasswordEncoder PasswordEncoder) {
        this.appUserRepository = appUserRepository;
        this.PasswordEncoder = PasswordEncoder;
    }

    public String login(String username, String password) {
        Optional<AppUser> userOptional = appUserRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            return "User not found!";
        }

        AppUser user = userOptional.get();

        // Check if the account is confirmed
        if (!user.isEnabled()) {
            return "Account is not confirmed!";
        }

        if (!PasswordEncoder.matches(password, user.getPassword())) {
            return "Invalid credentials!";
        }

        return "Login successful!";
    }
}
