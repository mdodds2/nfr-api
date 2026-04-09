package org.dodds.nfrapi.category;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateSubCategoryRequest {
    private String name;
    private String shortName;
    private String description;
    private Integer sortOrder;
}
