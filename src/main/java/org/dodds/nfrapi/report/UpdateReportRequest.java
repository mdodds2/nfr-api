package org.dodds.nfrapi.report;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReportRequest {
    private UUID userId;
    private String name;
    private String description;
    private List<UUID> requirementIds;
    private List<MeasurementDto> measurements;
}
