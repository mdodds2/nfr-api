package org.dodds.nfrapi.users;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Email already exists.");
    }
}
