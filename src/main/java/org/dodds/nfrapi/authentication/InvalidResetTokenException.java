package org.dodds.nfrapi.authentication;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {
        super("Invalid Password Reset Token");
    }
}
