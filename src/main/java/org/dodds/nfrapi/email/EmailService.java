package org.dodds.nfrapi.email;

public interface EmailService {
    void sendPasswordReset(String to, String resetLink);
}

