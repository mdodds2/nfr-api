package org.dodds.nfrapi.report;

import org.dodds.nfrapi.requirement.*;
import org.dodds.nfrapi.users.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Mock
    RequirementMapper requirementMapper;

    @Mock
    MeasurementMapper measurementMapper;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ReportService reportService;

    @DisplayName("Should create a mock report without error")
    @Test
    void shouldCreateAReport() {

        // Arrange
        var userId = UUID.randomUUID();
        var requirementId = UUID.randomUUID();
        var requirement = Requirement.builder().id(requirementId).title("Requirement 1").build();
        var requirementsDto = List.of(RequirementDto.builder().id(requirementId).title("Title 1").build());

        var measurement = Measurement.builder().id(UUID.randomUUID()).requirementId(requirementId).measurementMethod("method").build();
        var measurementDto = MeasurementDto.builder().id(UUID.randomUUID()).requirementId(requirementId).measurementMethod("method").build();
        var measurementsDto = List.of(measurementDto);

        CreateReportRequest request = CreateReportRequest.builder()
                .name("project name")
                .description("project description")
                .requirementIds(List.of(requirementId))
                .measurements(measurementsDto)
                .build();

        Report report = Report.builder().name(request.getName()).build();

        ReportDto dto = ReportDto.builder().
                id(UUID.randomUUID()).
                userId(userId).
                name(request.getName()).
                description(request.getDescription()).
                active(true).
                requirements(requirementsDto).
                measurements(measurementsDto).build();

        when(reportRepository.existsByUserIdAndName(userId, request.getName())).thenReturn(false);
        when(requirementRepository.findById(requirementId)).thenReturn(Optional.of(requirement));
        when(reportMapper.toEntity(request)).thenReturn(report);
        when(reportMapper.toDto(report)).thenReturn(dto);
        when(measurementMapper.toEntity(measurementDto)).thenReturn(measurement);

        // Act
        ReportDto result = reportService.createReport(userId, request);

        // Assert
        assertEquals(request.getName(), result.getName());
        assertEquals(userId, result.getUserId());
        verify(reportRepository).save(report);
    }

    @DisplayName("shouldThrowDuplicateReportWhenNameExistsForUser")
    @Test
    void shouldThrowDuplicateReportWhenNameExistsForUser() {

        UUID userId = UUID.randomUUID();
        // Arrange
        CreateReportRequest request = CreateReportRequest.builder()
                .name("project name")
                .description("project description")
                .requirementIds(List.of())
                .measurements(List.of())
                .build();

        when(reportRepository.existsByUserIdAndName(any(), any())).thenReturn(true);

        // Act

        // Assert
        assertThrows(DuplicateReportException.class, () -> reportService.createReport(userId, request));
        verify(reportRepository, never()).save(any());
    }

    @DisplayName("Should return a report")
    @Test
    void shouldReturnReportWhenIdExists() {
        UUID reportId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Requirement requirement = Requirement.builder().id(UUID.randomUUID()).build();
        Measurement measurement = Measurement.builder().id(UUID.randomUUID()).build();
        Report report = Report.builder()
                .id(reportId)
                .userId(userId)
                .name("report name")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement)).build();

        RequirementDto requirementDto = RequirementDto.builder().id(requirement.getId()).build();
        MeasurementDto measurementDto = new MeasurementDto(/* fill in its fields */);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportMapper.toDto(report)).thenReturn(ReportDto.builder().id(UUID.randomUUID()).build());
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);
        ReportDto result = reportService.getReport(reportId, userId);

        assertThat(result.getRequirements()).containsExactly(requirementDto);
        assertThat(result.getMeasurements()).containsExactly(measurementDto);
    }

    @DisplayName("Get report - not found")
    @Test
    void getReportNotFound() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.getReport(reportId, userId));
    }

    @DisplayName("Create report with an unknown requirement id")
    @Test
    void createReportWithAnUnknownRequirementId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID unknownReqtId = UUID.randomUUID();
        CreateReportRequest request = CreateReportRequest.builder()
                .name("project name")
                .description("project description")
                .requirementIds(List.of(unknownReqtId))
                .measurements(List.of())
                .build();

        Report report = Report.builder().name(request.getName()).build();

        when(reportRepository.existsByUserIdAndName(any(), any())).thenReturn(false);
        when(reportMapper.toEntity(request)).thenReturn(report);
        when(requirementRepository.findById(unknownReqtId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RequirementNotFoundException.class, () -> reportService.createReport(userId, request));
        verify(reportRepository, never()).save(any());
    }

    @DisplayName("Delete report - report found")
    @Test
    void deleteReportFound() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Report report = Report.builder()
                .id(reportId)
                .userId(userId)
                .name("report name")
                .reportRequirements(List.of())
                .measurements(Set.of())
                .build();

        // Act
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        reportService.deleteReport(reportId, userId);

        // Assert
        verify(reportRepository).delete(report);
    }

    @DisplayName("Delete report - report not found")
    @Test
    void deleteReportNotFound() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Report report = Report.builder().id(reportId).name("report name").reportRequirements(List.of()).measurements(Set.of()).build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.deleteReport(reportId, userId));
        verify(reportRepository, never()).delete(report);
    }

    @Test
    void shouldUpdateReport() {
        // Arrange
        var reportId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var requirementId = UUID.randomUUID();
        var requirement = Requirement.builder().id(requirementId).title("Requirement 1").build();
        var requirementsDto = List.of(RequirementDto.builder().id(requirementId).title("Title 1").build());

        var measurement = Measurement.builder().id(UUID.randomUUID()).requirementId(requirementId).measurementMethod("method").build();
        var measurementDto = MeasurementDto.builder().id(UUID.randomUUID()).requirementId(requirementId).measurementMethod("method").build();
        var measurementsDto = List.of(measurementDto);

        Report report = Report.builder()
                .id(reportId)
                .userId(userId)
                .name("report name")
                .reportRequirements(List.of())
                .measurements(Set.of())
                .build();

        UpdateReportRequest request = UpdateReportRequest.builder()
                .name("report name")
                .description("reportDescription")
                .requirementIds(List.of(requirementId))
                .measurements(measurementsDto)
                .build();

        ReportDto reportDto = ReportDto.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .name("report name")
                .description("report description")
                .active(true)
                .requirements(requirementsDto)
                .measurements(measurementsDto)
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(requirementRepository.findById(requirementId)).thenReturn(Optional.of(requirement));
        when(reportMapper.toDto(report)).thenReturn(reportDto);
        when(measurementMapper.toEntity(measurementDto)).thenReturn(measurement);

        // Act
        ReportDto result = reportService.updateReport(reportId, userId, request);

        // Assert
        assertEquals(userId, result.getUserId());
        verify(reportMapper).update(request, report);
        verify(reportRepository).save(report);
    }

    @DisplayName("Update report - report not found")
    @Test
    void shouldNotUpdateReportNotFound() {
        // Arrange
        var reportId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var report = Report.builder().id(reportId).name("report name").reportRequirements(List.of()).measurements(Set.of()).build();
        var request = UpdateReportRequest.builder()
                .name("report name")
                .description("reportDescription")
                .requirementIds(List.of())
                .measurements(List.of())
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.updateReport(reportId, userId, request));
        verify(reportRepository, never()).save(report);
    }

    @Test
    void shouldGetAllReportsSortedByName() {
        var requirement = Requirement.builder().id(UUID.randomUUID()).build();
        var measurement = Measurement.builder().id(UUID.randomUUID()).build();
        var report = Report.builder()
                .id(UUID.randomUUID())
                .name("report")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement))
                .build();
        var reportDto = ReportDto.builder().id(report.getId()).name("report").build();
        var requirementDto = RequirementDto.builder().id(requirement.getId()).build();
        var measurementDto = MeasurementDto.builder().id(measurement.getId()).build();

        when(reportRepository.findAllByUserId(any(), eq(Sort.by("name")))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);

        var result = reportService.getAllReports(UUID.randomUUID(), "name");

        assertThat(result).containsExactly(reportDto);
        assertThat(reportDto.getRequirements()).containsExactly(requirementDto);
        assertThat(reportDto.getMeasurements()).containsExactly(measurementDto);
     }

    @Test
    void shouldGetAllReportsSortedByCreatedAt() {
        var requirement = Requirement.builder().id(UUID.randomUUID()).build();
        var measurement = Measurement.builder().id(UUID.randomUUID()).build();
        var report = Report.builder()
                .id(UUID.randomUUID())
                .name("report")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement))
                .build();
        var reportDto = ReportDto.builder().id(report.getId()).name("report").build();
        var requirementDto = RequirementDto.builder().id(requirement.getId()).build();
        var measurementDto = MeasurementDto.builder().id(measurement.getId()).build();

        when(reportRepository.findAllByUserId(any(), eq(Sort.by("createdAt")))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);

        var result = reportService.getAllReports(UUID.randomUUID(), "createdAt");

        assertThat(result).containsExactly(reportDto);
        assertThat(reportDto.getRequirements()).containsExactly(requirementDto);
        assertThat(reportDto.getMeasurements()).containsExactly(measurementDto);
    }

    @Test
    void shouldGetAllReportsSortedByOther() {
        var requirement = Requirement.builder().id(UUID.randomUUID()).build();
        var measurement = Measurement.builder().id(UUID.randomUUID()).build();
        var report = Report.builder()
                .id(UUID.randomUUID())
                .name("report")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement))
                .build();
        var reportDto = ReportDto.builder().id(report.getId()).name("report").build();
        var requirementDto = RequirementDto.builder().id(requirement.getId()).build();
        var measurementDto = MeasurementDto.builder().id(measurement.getId()).build();

        when(reportRepository.findAllByUserId(any(), eq(Sort.by("name")))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);

        var result = reportService.getAllReports(UUID.randomUUID(), "other");

        assertThat(result).containsExactly(reportDto);
        assertThat(reportDto.getRequirements()).containsExactly(requirementDto);
        assertThat(reportDto.getMeasurements()).containsExactly(measurementDto);
    }

    @Test
    void shouldGetAllReportsSortedByNull() {
        var requirement = Requirement.builder().id(UUID.randomUUID()).build();
        var measurement = Measurement.builder().id(UUID.randomUUID()).build();
        var report = Report.builder()
                .id(UUID.randomUUID())
                .name("report")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement))
                .build();
        var reportDto = ReportDto.builder().id(report.getId()).name("report").build();
        var requirementDto = RequirementDto.builder().id(requirement.getId()).build();
        var measurementDto = MeasurementDto.builder().id(measurement.getId()).build();

        when(reportRepository.findAllByUserId(any(), eq(Sort.by("name")))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);

        var result = reportService.getAllReports(UUID.randomUUID(), null);

        assertThat(result).containsExactly(reportDto);
        assertThat(reportDto.getRequirements()).containsExactly(requirementDto);
        assertThat(reportDto.getMeasurements()).containsExactly(measurementDto);
    }

    @Test
    void shouldNotUpdateReportWrongUser() {
        // Arrange
        var reportId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var report = Report.builder()
                .id(reportId)
                .userId(UUID.randomUUID())
                .name("report name")
                .reportRequirements(List.of())
                .measurements(Set.of())
                .build();

        var request = UpdateReportRequest.builder()
                .name("report name")
                .description("reportDescription")
                .requirementIds(List.of())
                .measurements(List.of())
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.updateReport(reportId, userId, request));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void shouldNotDeleteReportWrongUser() {
        // Arrange
        var reportId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var report = Report.builder()
                .id(reportId)
                .userId(UUID.randomUUID())
                .name("report name")
                .reportRequirements(List.of())
                .measurements(Set.of())
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.deleteReport(reportId, userId));
        verify(reportRepository, never()).delete(any());
    }

}
