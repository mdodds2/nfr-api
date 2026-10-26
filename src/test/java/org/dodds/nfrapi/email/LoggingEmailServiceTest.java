package org.dodds.nfrapi.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class LoggingEmailServiceTest {

    private final LoggingEmailService service = new LoggingEmailService();

    @Test
    void shouldLogPasswordResetWithoutThrowing() {
        assertDoesNotThrow(() ->
                service.sendPasswordReset("user@example.com", "http://localhost/reset?token=abc"));    }

}
