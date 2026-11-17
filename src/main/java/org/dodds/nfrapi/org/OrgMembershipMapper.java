package org.dodds.nfrapi.org;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrgMembershipMapper {
    @Mapping(source = "organization.id", target = "id")
    @Mapping(source = "organization.name", target = "name")
    @Mapping(source = "organization.slug", target = "slug")
    OrgMembershipDto toDto(OrgMembership membership);
}