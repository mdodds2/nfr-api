package org.dodds.nfrapi.requirement;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.dodds.nfrapi.report.ReportDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class RequirementDto {
    private UUID id;
    private UUID subCategoryId;
    private String identifier;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String rationale;
    private String source;
    private String riskIfViolated;
    private LocalDateTime createdMoment;
    private LocalDateTime updatedMoment;
    private List<ReportDto> reports;
}
