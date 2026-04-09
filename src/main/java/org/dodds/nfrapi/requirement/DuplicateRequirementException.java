package org.dodds.nfrapi.requirement;

public class DuplicateRequirementException extends RuntimeException {
    public DuplicateRequirementException() {
        super("A requirement with the same identifier already exists");
    }
}
