package org.dodds.nfrapi.report;

import org.dodds.nfrapi.requirement.*;
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

    @InjectMocks
    ReportService reportService;

    @DisplayName("Should create a mock report without error")
    @Test
    void shouldCreateAReport() {

        // Arrange
        var requirementId = UUID.randomUUID();
        var requirement = Requirement.builder().id(requirementId).title("Requirement 1").build();
        var requirementsDto = List.of(RequirementDto.builder().id(requirementId).title("Title 1").build());

        var measurement = Measurement.builder().id(UUID.randomUUID()).requirementId(requirementId).measurementMethod("method").build();
        var measurementDto = MeasurementDto.builder().id(UUID.randomUUID()).requirementId(requirementId).measurementMethod("method").build();
        var measurementsDto = List.of(measurementDto);

        CreateReportRequest request = CreateReportRequest.builder()
                .userId(UUID.randomUUID())
                .name("project name")
                .description("project description")
                .requirementIds(List.of(requirementId))
                .measurements(measurementsDto)
                .build();

        Report report = Report.builder().name(request.getName()).build();

        ReportDto dto = ReportDto.builder().
                id(UUID.randomUUID()).
                userId(request.getUserId()).
                name(request.getName()).
                description(request.getDescription()).
                active(true).
                requirements(requirementsDto).
                measurements(measurementsDto).build();

        when(reportRepository.existsByUserIdAndName(request.getUserId(), request.getName())).thenReturn(false);
        when(requirementRepository.findById(requirementId)).thenReturn(Optional.of(requirement));
        when(reportMapper.toEntity(request)).thenReturn(report);
        when(reportMapper.toDto(report)).thenReturn(dto);
        when(measurementMapper.toEntity(measurementDto)).thenReturn(measurement);

        // Act
        ReportDto result = reportService.createReport(request);

        // Assert
        assertEquals(request.getName(), result.getName());
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

        when(reportRepository.existsByUserIdAndName(request.getUserId(), request.getName())).thenReturn(true);

        // Act

        // Assert
        assertThrows(DuplicateReportException.class, () -> reportService.createReport(request));
        verify(reportRepository, never()).save(any());
    }

    @DisplayName("Should return a report")
    @Test
    void shouldReturnReportWhenIdExists() {
        UUID uuid = UUID.randomUUID();

        Requirement requirement = Requirement.builder().id(UUID.randomUUID()).build();
        Measurement measurement = Measurement.builder().id(UUID.randomUUID()).build();
        Report report = Report.builder().id(uuid).name("report name")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement)).build();

        RequirementDto requirementDto = RequirementDto.builder().id(requirement.getId()).build();
        MeasurementDto measurementDto = new MeasurementDto(/* fill in its fields */);

        when(reportRepository.findById(uuid)).thenReturn(Optional.of(report));
        when(reportMapper.toDto(report)).thenReturn(ReportDto.builder().id(UUID.randomUUID()).build());
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);
        ReportDto result = reportService.getReport(uuid);

        assertThat(result.getRequirements()).containsExactly(requirementDto);
        assertThat(result.getMeasurements()).containsExactly(measurementDto);
    }

    @DisplayName("Get report - not found")
    @Test
    void getReportNotFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(reportRepository.findById(uuid)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.getReport(uuid));
    }

    @DisplayName("Get report - for user")
    @Test
    void getReportForUser() {
        // Arrange
        UUID userid = UUID.randomUUID();
        var requirement = Requirement.builder().id(UUID.randomUUID()).build();
        var measurement = Measurement.builder().id(UUID.randomUUID()).build();
        var report = Report.builder()
                .id(UUID.randomUUID())
                .name("report")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement))
                .build();
        var reportDto = ReportDto.builder().id(report.getId()).name("report").build();

        when(reportRepository.findAllByUserId(userid, Sort.by("name"))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        // Act
        var result = reportService.getReportsForUser(userid, "name");

        // Assert
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("Get report - for user sorted by created_at")
    @Test
    void getReportForUserSortedByDate() {
        // Arrange
        UUID userid = UUID.randomUUID();
        var requirement = Requirement.builder().id(UUID.randomUUID()).build();
        var measurement = Measurement.builder().id(UUID.randomUUID()).build();
        var report = Report.builder()
                .id(UUID.randomUUID())
                .name("report")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement))
                .build();
        var reportDto = ReportDto.builder().id(report.getId()).name("report").build();

        when(reportRepository.findAllByUserId(userid, Sort.by("created_at"))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        // Act
        var result = reportService.getReportsForUser(userid, "created_at");

        // Assert
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("Get report - for user sorted by other")
    @Test
    void getReportForUserSortedByOther() {
        // Arrange
        UUID userid = UUID.randomUUID();
        var requirement = Requirement.builder().id(UUID.randomUUID()).build();
        var measurement = Measurement.builder().id(UUID.randomUUID()).build();
        var report = Report.builder()
                .id(UUID.randomUUID())
                .name("report")
                .reportRequirements(List.of(requirement))
                .measurements(Set.of(measurement))
                .build();
        var reportDto = ReportDto.builder().id(report.getId()).name("report").build();

        when(reportRepository.findAllByUserId(userid, Sort.by("name"))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);

        // Act
        var result = reportService.getReportsForUser(userid, "other");

        // Assert
        assertThat(result.size()).isEqualTo(1);
    }


    @DisplayName("Create report with an unknown requirement id")
    @Test
    void createReportWithAnUnknownRequirementId() {
        // Arrange
        UUID unknownId = UUID.randomUUID();
        CreateReportRequest request = new CreateReportRequest(
                UUID.randomUUID(),
                "project name",
                "project description",
                List.of(unknownId),
                List.of());
        Report report = Report.builder().name(request.getName()).build();

        when(reportRepository.existsByUserIdAndName(request.getUserId(), request.getName())).thenReturn(false);
        when(reportMapper.toEntity(request)).thenReturn(report);
        when(requirementRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RequirementNotFoundException.class, () -> reportService.createReport(request));
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

        when(reportRepository.findById(uuid)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.deleteReport(uuid));
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

        Report report = Report.builder().id(reportId).name("report name").reportRequirements(List.of()).measurements(Set.of()).build();
        UpdateReportRequest request = UpdateReportRequest.builder()
                .userId(userId)
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
        ReportDto result = reportService.updateReport(reportId, request);

        // Assert
        assertEquals(request.getUserId(), result.getUserId());
        verify(reportMapper).update(request, report);
        verify(reportRepository).save(report);
    }

    @DisplayName("Update report - report not found")
    @Test
    void shouldNotUpdateReportNotFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().id(uuid).name("report name").reportRequirements(List.of()).measurements(Set.of()).build();
        UpdateReportRequest request = new UpdateReportRequest(
                uuid,
                "report name",
                "reportDescription",
                List.of(),
                List.of());

        when(reportRepository.findById(uuid)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.updateReport(uuid, request));
        verify(reportRepository, never()).save(report);
    }

    @Test
    void shouldGetAllReportsSortedByName() throws Exception {
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

        when(reportRepository.findAll(Sort.by("name"))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);

        var result = reportService.getAllReports("name");

        assertThat(result).containsExactly(reportDto);
        assertThat(reportDto.getRequirements()).containsExactly(requirementDto);
        assertThat(reportDto.getMeasurements()).containsExactly(measurementDto);
     }

    @Test
    void shouldGetAllReportsSortedByCreatedAt() throws Exception {
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

        when(reportRepository.findAll(Sort.by("created_at"))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);

        var result = reportService.getAllReports("created_at");

        assertThat(result).containsExactly(reportDto);
        assertThat(reportDto.getRequirements()).containsExactly(requirementDto);
        assertThat(reportDto.getMeasurements()).containsExactly(measurementDto);
    }

    @Test
    void shouldGetAllReportsSortedByOther() throws Exception {
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

        when(reportRepository.findAll(Sort.by("name"))).thenReturn(List.of(report));
        when(reportMapper.toDto(report)).thenReturn(reportDto);
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);
        when(measurementMapper.toDto(measurement)).thenReturn(measurementDto);

        var result = reportService.getAllReports("other");

        assertThat(result).containsExactly(reportDto);
        assertThat(reportDto.getRequirements()).containsExactly(requirementDto);
        assertThat(reportDto.getMeasurements()).containsExactly(measurementDto);
    }

}
