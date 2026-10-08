package org.dodds.nfrapi.report;

import org.dodds.nfrapi.category.Category;
import org.dodds.nfrapi.category.SubCategory;
import org.dodds.nfrapi.requirement.Requirement;
import org.dodds.nfrapi.requirement.RequirementNotFoundException;
import org.dodds.nfrapi.requirement.RequirementRepository;
import org.hibernate.sql.Update;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

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

    @DisplayName("shouldThrowDuplicateReportWhenNameExistsForUser")
    @Test
    void shouldThrowDuplicateReportWhenNameExistsForUser() {

        // Arrange
        CreateReportRequest request = new CreateReportRequest(
                UUID.randomUUID(),
                "project name",
                "project description",
                List.of(),
                List.of());

        when(reportRepository.existsByUserIdAndName(request.userId(), request.name())).thenReturn(true);

        // Act

        // Assert
        assertThrows(DuplicateReportException.class, () -> reportService.createReport(request));
        verify(reportRepository, never()).save(any());
    }

    @DisplayName("Should return a report")
    @Test
    void shouldReturnReportWhenIdExists() {
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

        when(reportRepository.findById(uuid)).thenReturn(Optional.of(report));
        when(reportMapper.toDto(report)).thenReturn(dto);

        // Act
        ReportDto result = reportService.getReport(uuid);

        // Assert
        assertEquals(uuid, result.getId());
        assertTrue(result.getRequirements().isEmpty());
        assertTrue(result.getMeasurements().isEmpty());
    }

    @DisplayName("Get report - not found")
    @Test
    void getReportNotFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(reportRepository.findById(uuid)).thenThrow(new ReportNotFoundException());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.getReport(uuid));
    }

    @DisplayName("Create report with an unknown requirement id")
    @Test
    void createReportWithAnUnknownRequirementId() {
        // Arrange
        List<UUID> reqts = new ArrayList<>();
        reqts.add(UUID.randomUUID());

        CreateReportRequest request = new CreateReportRequest(
                UUID.randomUUID(),
                "project name",
                "project description",
                reqts,
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

        Category category = new Category(UUID.randomUUID(), "name", "short name", "description", Integer.valueOf(1), true, null, null, null, List.of());

        SubCategory subCategory = new SubCategory(
                UUID.randomUUID(),
                "name",
                "short name",
                "desc",
                Integer.valueOf(1),
                true,
                null,
                null,
                category);

        Requirement requirement = new Requirement(
                UUID.randomUUID(),
                "requirement identifier",
                "title" ,
                "description",
                "HIGH",
                "ACTIVE",
                "RATIONALE",
                "SOURCE",
                "RISK IF VIOLATED",
                null,
                null,
                subCategory,
                null);


        when(reportRepository.existsByUserIdAndName(request.userId(), request.name())).thenReturn(false);
        when(requirementRepository.findById(requirement.getId())).thenThrow(new RequirementNotFoundException());
        when(reportMapper.toEntity(request)).thenReturn(report);

        // Act
        ReportDto result = reportService.createReport(request);

        // Assert
        assertEquals(request.name(), result.getName());
        verify(reportRepository, never()).save(any());
    }

    @DisplayName("Delete report - report found")
    @Test
    void deleteReportFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().id(uuid).name("report name").reportRequirements(List.of()).measurements(Set.of()).build();

        // Act
        when(reportRepository.findById(uuid)).thenReturn(Optional.of(report));
        reportService.deleteReport(uuid);

        // Assert
        verify(reportRepository).delete(report);
    }

    @DisplayName("Delete report - report not found")
    @Test
    void deleteReportNotFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().id(uuid).name("report name").reportRequirements(List.of()).measurements(Set.of()).build();

        when(reportRepository.findById(uuid)).thenThrow(new ReportNotFoundException());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.deleteReport(uuid));
        verify(reportRepository, never()).delete(report);
    }

    @DisplayName("Update report - report found")
    @Test
    void updateReportFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().id(uuid).name("report name").reportRequirements(List.of()).measurements(Set.of()).build();
        UpdateReportRequest request = new UpdateReportRequest(
                uuid,
                "report name",
                "reportDescription",
                List.of(),
                List.of());
        ReportDto reportDto = new ReportDto(
                uuid,
                uuid,
                "report name",
                "report description",
                true,
                null,
                null,
                List.of(),
                List.of());

        when(reportRepository.findById(uuid)).thenReturn(Optional.of(report));
        doAnswer(inv -> {
            return report;
        }).when(reportMapper).update(request, report);
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        // Act
        ReportDto result = reportService.updateReport(uuid, request);

        // Assert
        assertEquals(request.getUserId(), result.getUserId());
        verify(reportRepository).save(report);
    }

    @DisplayName("Update report - report not found")
    @Test
    void updateReportNotFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().id(uuid).name("report name").reportRequirements(List.of()).measurements(Set.of()).build();
        UpdateReportRequest request = new UpdateReportRequest(
                uuid,
                "report name",
                "reportDescription",
                List.of(),
                List.of());

        when(reportRepository.findById(uuid)).thenThrow(new ReportNotFoundException());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.updateReport(uuid, request));
        verify(reportRepository, never()).save(report);
    }

}
