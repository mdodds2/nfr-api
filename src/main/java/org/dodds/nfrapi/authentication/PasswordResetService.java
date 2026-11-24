package org.dodds.nfrapi.authentication;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.common.TokenHasher;
import org.dodds.nfrapi.email.EmailService;
import org.dodds.nfrapi.users.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class PasswordResetService {

    private static final String RESET_URL = "http://localhost:5173/reset-password";

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final TokenHasher tokenHasher;

    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmail(email).ifPresent( user -> {
            var rawToken = tokenHasher.generateToken();
            var token = PasswordResetToken.builder()
                    .userId(user.getId())
                    .tokenHash(tokenHasher.hash(rawToken))
                    .expiresAt(LocalDateTime.now().plusMinutes(30))
                    .build();

            tokenRepository.deleteByUserId(user.getId());
            tokenRepository.save(token);
            emailService.sendPasswordReset(user.getEmail(), RESET_URL + "?token=" + rawToken);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        var resetToken = tokenRepository.findByTokenHash(tokenHasher.hash(token)).orElseThrow(InvalidResetTokenException::new);
        if(!resetToken.isUsable()) throw new InvalidResetTokenException();

        var user = userRepository.findById(resetToken.getUserId()).orElseThrow(InvalidResetTokenException::new);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
    }

}
