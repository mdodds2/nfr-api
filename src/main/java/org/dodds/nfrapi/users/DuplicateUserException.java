package org.dodds.nfrapi.users;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException() {
        super("User already exists.");
    }
}
