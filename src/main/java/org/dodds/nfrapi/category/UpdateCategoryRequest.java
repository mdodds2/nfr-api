package org.dodds.nfrapi.category;

import lombok.Builder;

@Builder
public record UpdateCategoryRequest(
    String name,
    String shortName,
    String description,
    Integer sortOrder,
    Boolean active
) {}
