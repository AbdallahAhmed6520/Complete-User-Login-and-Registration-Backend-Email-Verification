package com.example.demo.Controller;

import com.example.demo.email.EmailRequest;
import com.example.demo.dto.RegistrationRequest;
import com.example.demo.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(registrationService.register(request));
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

    @PostMapping("/resendToken")
    public ResponseEntity<String> resendToken(@RequestBody EmailRequest request) {
        return ResponseEntity.ok(registrationService.resendToken(request.getEmail()));
    }
}
