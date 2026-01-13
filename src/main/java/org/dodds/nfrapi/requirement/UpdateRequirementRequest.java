package org.dodds.nfrapi.requirement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
public class UpdateRequirementRequest {
    private UUID groupId;
    private String name;
    private String description;
    private String background;
    private Boolean active;
}
