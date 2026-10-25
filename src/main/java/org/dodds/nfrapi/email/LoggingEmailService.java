package org.dodds.nfrapi.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggingEmailService implements EmailService {
    @Override
    public void sendPasswordReset(String to, String resetLink) {
        log.info("[EMAIL STUB] Password reset for {}: {}", to, resetLink);
    }
}
