package org.dodds.nfrapi.report;

import org.dodds.nfrapi.requirement.Requirement;
import org.dodds.nfrapi.requirement.RequirementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @InjectMocks
    ReportRepository reportRepository;

    @InjectMocks
    RequirementRepository requirementRepository;

    @InjectMocks
    ReportService reportService;

    @DisplayName("Should create a mock report without error")
    @Test
    void shouldCreateAReport() {

        UUID userId = UUID.randomUUID();
        String name = "project name";
        String description = "project description";
        List<UUID> requirementIds = new ArrayList<>();
        List<MeasurementDto> measurements = new ArrayList<>();

        // Arrange
        CreateReportRequest request  = new CreateReportRequest(
                userId,
                name,
                description,
                requirementIds,
                measurements
        );

        // Act
        when(reportRepository.existsByUserIdAndName(request.userId(), request.name())).thenReturn(false);
        when(requirementRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        reportService.createReport(request);
    }
}
