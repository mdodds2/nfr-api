package org.dodds.nfrapi.org;

public class OrganizationNotFoundException extends RuntimeException {
    public OrganizationNotFoundException() {
        super("Organization not found.");
    }
}
