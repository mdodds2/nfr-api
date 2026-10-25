package org.dodds.nfrapi.authentication;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.email.EmailService;
import org.dodds.nfrapi.users.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@AllArgsConstructor
@Service
public class PasswordResetService {

    private static final String RESET_URL = "http://localhost:5173/reset-password";

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;

    @Transactional
    public void requestReset(String emailId) {
        userRepository.findByEmail(emailId).ifPresent( user -> {
            var rawToken = generateToken();
            var token = PasswordResetToken.builder()
                    .userId(user.getId())
                    .tokenHash(hash(rawToken))
                    .expiresAt(LocalDateTime.now().plusMinutes(30))
                    .build();

            tokenRepository.deleteByUserId(user.getId());
            tokenRepository.save(token);
            emailService.sendPasswordReset(user.getEmail(), RESET_URL + "?token=" + rawToken);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        var resetToken = tokenRepository.findByTokenHash(hash(token)).orElseThrow(InvalidResetTokenException::new);
        if(!resetToken.isUsable()) throw new InvalidResetTokenException();

        var user = userRepository.findById(resetToken.getUserId()).orElseThrow(InvalidResetTokenException::new);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest); // 64 hex chars
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
