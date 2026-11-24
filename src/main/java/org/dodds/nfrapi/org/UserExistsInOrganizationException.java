package org.dodds.nfrapi.org;

public class UserExistsInOrganizationException extends RuntimeException {
    public UserExistsInOrganizationException() {
        super("User already part of organization.");
    }
}
