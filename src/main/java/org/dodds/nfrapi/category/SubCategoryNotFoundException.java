package org.dodds.nfrapi.category;

public class SubCategoryNotFoundException extends CategoryException {
    public SubCategoryNotFoundException() {
        super("Subcategory not found.");
    }
}
