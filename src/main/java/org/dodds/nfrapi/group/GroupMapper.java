package org.dodds.nfrapi.group;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupDto toDto(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    Group toEntityFromCreateGroupRequest(CreateGroupRequest request);

    Group toEntity(GroupDto groupDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateGroupRequest request, @MappingTarget Group group);
}
