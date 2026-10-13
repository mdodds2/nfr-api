package org.dodds.nfrapi.report;

import lombok.*;
import org.dodds.nfrapi.requirement.RequirementDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private List<RequirementDto> requirements;
    private List<MeasurementDto> measurements;
}
