package org.dodds.nfrapi.requirement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dodds.nfrapi.common.Priority;
import org.dodds.nfrapi.common.Status;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateRequirementRequest {
    private UUID subCategoryId;
    private String identifier;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private String targetValue;
    private String thresholdValue;
    private String unit;
    private String measurementMethod;
    private String rationale;
    private String source;
    private String riskIfViolated;
}
