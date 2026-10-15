package org.dodds.nfrapi.report;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReportRequest {
    @NotNull
    private UUID userId;

    @NotBlank
    @Size(max=255)
    private String name;

    @NotBlank
    private String description;

    private List<UUID> requirementIds;

    @Valid
    private List<MeasurementDto> measurements;
}
