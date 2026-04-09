package org.dodds.nfrapi.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class UpdateReportRequest {
    private UUID userId;
    private String name;
    private String description;
    private List<UUID> requirementIds;
    private List<MeasurementDto> measurements;
}
