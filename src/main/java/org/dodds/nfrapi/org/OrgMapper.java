package org.dodds.nfrapi.org;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrgMapper {

    OrgDto toDto(Organization organization);

    //Organization toEntity(CreateReportRequest request);
}
