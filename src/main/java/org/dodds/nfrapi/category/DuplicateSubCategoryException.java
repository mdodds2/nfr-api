package org.dodds.nfrapi.category;

public class DuplicateSubCategoryException extends CategoryException {
    public DuplicateSubCategoryException() {
        super("SubCategory already exists");
    }
}
