package com.example.demo.registration.passwordReset;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordResetRequest {
    private String email;
    private String newPassword;
}
