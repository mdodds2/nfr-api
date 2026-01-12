package org.dodds.nfrapi.requirement;

import org.dodds.nfrapi.group.CreateGroupRequest;
import org.dodds.nfrapi.group.Group;
import org.dodds.nfrapi.group.GroupDto;
import org.dodds.nfrapi.group.UpdateGroupRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RequirementMapper {

    RequirementDto toDto(Requirement group);

    Requirement toEntity(RequirementDto requirementDto);

}