package org.dodds.nfrapi.org;

public class LastOrgAdminException extends RuntimeException {
    public LastOrgAdminException() {
        super("An organization needs at least one org admin");
    }
}
