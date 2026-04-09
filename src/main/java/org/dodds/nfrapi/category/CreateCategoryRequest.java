package org.dodds.nfrapi.category;

import lombok.Data;

@Data
public class CreateCategoryRequest {
    private String name;
    private String shortName;
    private String description;
    private Integer sortOrder;
    private String image;
}
