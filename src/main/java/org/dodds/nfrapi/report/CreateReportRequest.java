package org.dodds.nfrapi.report;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    private UUID userId;

    @NotNull
    @Size(max=255)
    private String name;

    @NotBlank
    private String description;

    private List<UUID> requirementIds;

    @Valid
    private List<MeasurementDto> measurements;
}
