package org.dodds.nfrapi.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dodds.nfrapi.requirement.RequirementDto;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class GroupDto {

    private UUID id;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime createdMoment;
    private LocalDateTime updatedMoment;
}
