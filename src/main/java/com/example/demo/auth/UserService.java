package com.example.demo.auth;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(AppUserRepository appUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.appUserRepository = appUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String login(String username, String password) {
        Optional<AppUser> userOptional = appUserRepository.findByEmail(username);
        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                return "Login successful!";
            } else {
                return "Invalid credentials!";
            }
        } else {
            return "User not found!";
        }
    }
}
