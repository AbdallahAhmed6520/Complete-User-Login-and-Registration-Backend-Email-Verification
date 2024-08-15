package com.example.demo.Controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.service.AppUserService;
import com.example.demo.service.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = appUserService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        appUserService.updateUser(userId, request, userDetails);
        return ResponseEntity.ok("User updated successfully");

    }
}
