package org.dodds.nfrapi.category;

import lombok.Builder;

@Builder
public record CreateSubCategoryRequest(
    String name,
    String shortName,
    String description,
    Integer sortOrder
) {}
