package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateUserRequest {
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    private String email;
}