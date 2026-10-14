package org.dodds.nfrapi.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {
    private UUID userId;
    private String name;
    private String description;
    private List<UUID> requirementIds;
    private List<MeasurementDto> measurements;
}
