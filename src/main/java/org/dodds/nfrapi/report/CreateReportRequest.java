package org.dodds.nfrapi.report;

import java.util.List;
import java.util.UUID;

public record CreateReportRequest (
    UUID userId,
    String name,
    String description,
    List<UUID> requirementIds,
    List<MeasurementDto> measurements
) {}
