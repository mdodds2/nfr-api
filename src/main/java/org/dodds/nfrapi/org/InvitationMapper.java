package org.dodds.nfrapi.org;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    InvitationDto toDto(Invitation invitation);

    @Mapping(target = "tokenHash", ignore = true)
    Invitation toEntity(CreateInvitationRequest request);
}
