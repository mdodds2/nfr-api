package org.dodds.nfrapi.report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MeasurementMapper {

    MeasurementDto toDto(Measurement measurement);

    @Mapping(target = "report", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    Measurement toEntity(MeasurementDto measurementDto);

}
