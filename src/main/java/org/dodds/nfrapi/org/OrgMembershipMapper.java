package org.dodds.nfrapi.org;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrgMembershipMapper {
    @Mapping(source = "organization.id", target = "id")
    @Mapping(source = "organization.name", target = "name")
    @Mapping(source = "organization.slug", target = "slug")
    OrgMembershipDto toDto(OrgMembership membership);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name",   source = "user.name")
    @Mapping(target = "email",  source = "user.email")
    List<OrgMemberDto> toMemberDtos(List<OrgMembership> memberships);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name",   source = "user.name")
    @Mapping(target = "email",  source = "user.email")
    OrgMemberDto toMemberDto(OrgMembership membership);

}