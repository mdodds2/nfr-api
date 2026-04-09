package org.dodds.nfrapi.authentication;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.users.User;
import org.dodds.nfrapi.users.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (UUID) authentication.getPrincipal();
        return userRepository.findById(userId).orElse(null);
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        if(authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    public Jwt refreshAccessToken(String refreshToken) {
        var jwt = jwtService.parseToken(refreshToken);
        if(jwt == null || jwt.isExpired()) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        return jwtService.generateAccessToken(user);
    }
}
