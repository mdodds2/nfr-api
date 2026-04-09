package org.dodds.nfrapi.report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "requirements", ignore = true)
    ReportDto toDto(Report report);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "reportRequirements", ignore = true)
    @Mapping(target = "measurements", ignore = true)
    Report toEntity(CreateReportRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "reportRequirements", ignore = true)
    @Mapping(target = "measurements", ignore = true)
    void update(UpdateReportRequest request, @MappingTarget Report report);
}
