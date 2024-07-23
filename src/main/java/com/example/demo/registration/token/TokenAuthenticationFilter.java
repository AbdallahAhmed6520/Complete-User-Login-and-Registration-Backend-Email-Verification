package com.example.demo.registration.token;

import com.example.demo.appuser.AppUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final ConfirmationTokenService confirmationTokenService;

    public TokenAuthenticationFilter(ConfirmationTokenService confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && !token.isEmpty()) {
            Optional<ConfirmationToken> optionalToken = confirmationTokenService.getToken(token);
            if (optionalToken.isPresent() && !optionalToken.get().isExpired()) {
                // التحقق من صلاحية الرمز
                AppUser user = optionalToken.get().getAppUser();
                // إعداد الجلسة مع معلومات المستخدم
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
