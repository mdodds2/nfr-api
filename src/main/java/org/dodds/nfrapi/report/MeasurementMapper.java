package org.dodds.nfrapi.report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MeasurementMapper {

    MeasurementDto toDto(Measurement measurement);

    Measurement toEntity(MeasurementDto measurementDto);

}
