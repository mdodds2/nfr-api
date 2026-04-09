package org.dodds.nfrapi.category;

import lombok.Data;

@Data
public class UpdateCategoryRequest {
    private String name;
    private String shortName;
    private String description;
    private Integer sortOrder;
    private Boolean active;
}
