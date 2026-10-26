package org.dodds.nfrapi.authentication;

import org.dodds.nfrapi.email.EmailService;
import org.dodds.nfrapi.users.User;
import org.dodds.nfrapi.users.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock
    PasswordResetTokenRepository tokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    PasswordResetService passwordResetService;

    @Test
    void shouldCreateTokenAndSendEmailForKnownUser() {
        var user = User.builder().id(UUID.randomUUID()).email("user@domain.com").build();
        when(userRepository.findByEmail("user@domain.com")).thenReturn(Optional.of(user));
        passwordResetService.requestReset("user@domain.com");

        var tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        var linkCaptor = ArgumentCaptor.forClass(String.class);

        verify(tokenRepository).deleteByUserId(user.getId());
        verify(tokenRepository).save(tokenCaptor.capture());
        verify(emailService).sendPasswordReset(eq("user@domain.com"), linkCaptor.capture());

        var saved = tokenCaptor.getValue();
        var rawToken = linkCaptor.getValue().substring(linkCaptor.getValue().indexOf("token=") + 6);

        assertEquals(user.getId(), saved.getUserId());
        assertNotEquals(rawToken, saved.getTokenHash());   // stored hashed, not raw
        assertEquals(64, saved.getTokenHash().length());
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void shouldDoNothingForUnknownEmail() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        passwordResetService.requestReset("nobody@domain.com");
        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordReset(any(), any());
    }

    @Test
    void shouldResetPassword() {
        var userId = UUID.randomUUID();
        var token = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tokenHash("TOKEN_HASH")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
        var user = User.builder().id(userId).email("user@domain.com").build();

        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NEW_PASSWORD")).thenReturn("ENCODED");

        passwordResetService.resetPassword("RAW_TOKEN", "NEW_PASSWORD");

        assertEquals("ENCODED", user.getPassword());
        assertNotNull(token.getUsedAt());
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void shouldNotResetPasswordUnknownToken()  {
        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());
        assertThrows(InvalidResetTokenException.class, () -> passwordResetService.resetPassword("RAW_TOKEN", "NEW_PASSWORD"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotResetPasswordExpiredToken()  {
        var userId = UUID.randomUUID();
        var token = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tokenHash("TOKEN_HASH")
                .expiresAt(LocalDateTime.now().minusMinutes(15))
                .build();

        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        assertThrows(InvalidResetTokenException.class, () -> passwordResetService.resetPassword("RAW_TOKEN", "NEW_PASSWORD"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotResetPasswordAlreadyUsed()  {
        var userId = UUID.randomUUID();
        var token = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .usedAt(LocalDateTime.now())
                .tokenHash("TOKEN_HASH")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        assertThrows(InvalidResetTokenException.class, () -> passwordResetService.resetPassword("RAW_TOKEN", "NEW_PASSWORD"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotResetPasswordUserNotFound()  {
        var userId = UUID.randomUUID();
        var token = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tokenHash("TOKEN_HASH")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidResetTokenException.class, () -> passwordResetService.resetPassword("RAW_TOKEN", "NEW_PASSWORD"));
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Included for coverage purposes only")
    void shouldNotResetPasswordAlgorithmNotFound()  {
        try (MockedStatic<MessageDigest> md = mockStatic(MessageDigest.class)) {
            md.when(() -> MessageDigest.getInstance("SHA-256"))
                    .thenThrow(new NoSuchAlgorithmException("boom"));
            assertThrows(IllegalStateException.class,
                    () -> passwordResetService.resetPassword("token", "newPassword123"));
        }
    }

}
