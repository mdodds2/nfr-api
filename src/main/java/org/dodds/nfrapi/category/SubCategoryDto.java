package org.dodds.nfrapi.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
