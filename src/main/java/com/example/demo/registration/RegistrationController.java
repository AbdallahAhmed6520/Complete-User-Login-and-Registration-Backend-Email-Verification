package com.example.demo.registration;

import com.example.demo.email.EmailRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
    @PostMapping("/resendToken")
    public ResponseEntity<String> resendToken(@RequestBody EmailRequest request) {
        return ResponseEntity.ok(registrationService.resendToken(request.getEmail()));
    }
}
