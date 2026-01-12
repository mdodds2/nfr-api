package org.dodds.nfrapi.requirement;

public class RequirementNoFoundException extends RuntimeException {
    public RequirementNoFoundException() {
        super("Requirement not found");
    }
}
