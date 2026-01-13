package org.dodds.nfrapi.requirement;

public class RequirementNotFoundException extends RuntimeException {
    public RequirementNotFoundException() {
        super("Requirement not found");
    }
}
