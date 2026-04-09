package org.dodds.nfrapi.category;

public class CategoryNotFoundException extends CategoryException {
    public CategoryNotFoundException() {
        super("Category not found.");
    }
}
