package com.example.demo.model;

import com.example.demo.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
    public PasswordResetToken(String token, LocalDateTime expiresAt, AppUser appUser) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }
}
