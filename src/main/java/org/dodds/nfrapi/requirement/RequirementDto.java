package org.dodds.nfrapi.requirement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dodds.nfrapi.common.Priority;
import org.dodds.nfrapi.common.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RequirementDto {
    private UUID id;
    private UUID subCategoryId;
    private String identifier;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private String rationale;
    private String source;
    private String riskIfViolated;
    private LocalDateTime createdMoment;
    private LocalDateTime updatedMoment;
}
