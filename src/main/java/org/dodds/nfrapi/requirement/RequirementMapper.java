package org.dodds.nfrapi.requirement;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RequirementMapper {

    @Mapping(target = "groupId", source="group.id")
    RequirementDto toDto(Requirement requirement);

    @Mapping(target = "group", ignore = true)
    Requirement toEntity(RequirementDto requirementDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    @Mapping(target = "group", ignore = true)
    Requirement toEntityFromCreateRequirementRequest(CreateRequirementRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    @Mapping(target = "group", ignore = true)
    Requirement toEntityFromUpdateRequirementRequest(UpdateRequirementRequest request);

    @Mapping(target = "group", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateRequirementRequest request, @MappingTarget Requirement requirement);
}