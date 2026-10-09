package org.dodds.nfrapi.report;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementDto {
    private UUID id;
    private UUID requirementId;
    private String targetValue;
    private String thresholdValue;
    private String unit;
    private String measurementMethod;
    private String trigger;
    private String context;
    private String systemResponse;
    private String owner;
    private String acceptanceCriteria;
    private String notes;
}
