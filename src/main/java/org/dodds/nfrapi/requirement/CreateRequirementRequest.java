package org.dodds.nfrapi.requirement;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class CreateRequirementRequest {
    private UUID subCategoryId;
    private String identifier;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String targetValue;
    private String thresholdValue;
    private String unit;
    private String measurementMethod;
    private String rationale;
    private String source;
    private String riskIfViolated;
}
