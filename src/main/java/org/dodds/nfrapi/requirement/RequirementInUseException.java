package org.dodds.nfrapi.requirement;

public class RequirementInUseException extends RuntimeException {
    public RequirementInUseException() {
        super("Unable to delete.  This Quality Attribute is in use by existing reports");
    }
}
