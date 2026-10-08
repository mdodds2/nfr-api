package org.dodds.nfrapi.report;

import org.dodds.nfrapi.requirement.Requirement;
import org.dodds.nfrapi.requirement.RequirementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    ReportRepository reportRepository;

    @Mock
    RequirementRepository requirementRepository;

    @Mock
    ReportMapper reportMapper;

    @InjectMocks
    ReportService reportService;

    @DisplayName("Should create a mock report without error")
    @Test
    void shouldCreateAReport() {

        // Arrange
        CreateReportRequest request = new CreateReportRequest(
                UUID.randomUUID(),
                "project name",
                "project description",
                List.of(),
                List.of());

        Report report = Report.builder().name(request.name()).build();
        ReportDto dto = new ReportDto(UUID.randomUUID(),
                request.userId(),
                request.name(),
                request.description(),
                true,
                null,
                null,
                null,
                null);

        when(reportRepository.existsByUserIdAndName(request.userId(), request.name())).thenReturn(false);
        when(reportMapper.toEntity(request)).thenReturn(report);
        when(reportMapper.toDto(report)).thenReturn(dto);

        // Act
        ReportDto result = reportService.createReport(request);

        // Assert
        assertEquals(request.name(), result.getName());
        verify(reportRepository).save(report);

    }

    @DisplayName("Should create a mock report without error")
    @Test
    void existsByUserIdAndName() {

        // Arrange
        CreateReportRequest request = new CreateReportRequest(
                UUID.randomUUID(),
                "project name",
                "project description",
                List.of(),
                List.of());

        Report report = Report.builder().name(request.name()).build();
        ReportDto dto = new ReportDto(UUID.randomUUID(),
                request.userId(),
                request.name(),
                request.description(),
                true,
                null,
                null,
                null,
                null);

        when(reportRepository.existsByUserIdAndName(request.userId(), request.name())).thenReturn(true);

        // Act

        // Assert
        assertThrows(DuplicateReportException.class, () -> reportService.createReport(request));

    }

    @DisplayName("Should return a report")
    @Test
    void findById() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().name("report name").reportRequirements(List.of()).measurements(Set.of()).build();
        ReportDto dto = new ReportDto(uuid,
                report.getUserId(),
                report.getName(),
                report.getDescription(),
                true,
                null,
                null,
                null,
                null);

        // Act
        when(reportRepository.findById(uuid)).thenReturn(Optional.ofNullable(report));
        when(reportMapper.toDto(any())).thenReturn(dto);
        ReportDto result = reportService.getReport(uuid);

        // Assert
        assertEquals(uuid, result.getId());
    }
}
