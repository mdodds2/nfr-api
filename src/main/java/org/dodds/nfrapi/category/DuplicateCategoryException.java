package org.dodds.nfrapi.category;

public class DuplicateCategoryException extends CategoryException {
    public DuplicateCategoryException() {
        super("Category already exists");
    }
}
