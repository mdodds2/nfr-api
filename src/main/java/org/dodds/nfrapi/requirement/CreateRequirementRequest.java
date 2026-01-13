package org.dodds.nfrapi.requirement;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class CreateRequirementRequest {
    private UUID groupId;
    private String name;
    private String description;
    private String background;

}
