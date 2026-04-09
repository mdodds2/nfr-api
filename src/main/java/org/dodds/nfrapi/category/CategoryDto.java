package org.dodds.nfrapi.category;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CategoryDto {
    private UUID id;
    private String name;
    private String shortName;
    private String description;
    private Integer sortOrder;
    private Boolean active;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private List<SubCategoryDto> subCategories;
}
