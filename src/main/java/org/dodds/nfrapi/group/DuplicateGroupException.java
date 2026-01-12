package org.dodds.nfrapi.group;

public class DuplicateGroupException extends RuntimeException {
    public DuplicateGroupException() {
        super("Group name has to be unique.");
    }
}
