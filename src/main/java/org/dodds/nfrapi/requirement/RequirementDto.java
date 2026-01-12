package org.dodds.nfrapi.requirement;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RequirementDto {

    private UUID id;
    private UUID groupId;
    private String name;
    private String description;
    private String background;
    private Boolean active;
    private LocalDateTime createdMoment;
    private LocalDateTime updatedMoment;
}
