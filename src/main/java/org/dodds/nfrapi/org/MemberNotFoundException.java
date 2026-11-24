package org.dodds.nfrapi.org;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("Organization member not found.");
    }
}
