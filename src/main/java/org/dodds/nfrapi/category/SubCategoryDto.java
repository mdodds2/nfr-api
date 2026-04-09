package org.dodds.nfrapi.category;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubCategoryDto {
    private UUID id;
    private UUID categoryId;
    private String name;
    private String shortName;
    private String description;
    private Integer sortOrder;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
