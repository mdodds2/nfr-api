package org.dodds.nfrapi.category;

import lombok.Builder;

@Builder
public record CreateCategoryRequest (
    String name,
    String shortName,
    String description,
    Integer sortOrder,
    String image
) {}
