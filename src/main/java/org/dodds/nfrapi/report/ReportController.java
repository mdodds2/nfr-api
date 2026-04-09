package org.dodds.nfrapi.report;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.common.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Reports controller description")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public Iterable<ReportDto> getAllReports(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy ){
        return reportService.getAllReports(sortBy);
    }

    @GetMapping("/{id}")
    public ReportDto getReport(@PathVariable UUID id) {
        return reportService.getReport(id);
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @Valid
            @RequestBody CreateReportRequest request, UriComponentsBuilder uriBuilder) {
        var reportDto = reportService.createReport(request);
        var uri = uriBuilder.path("/reports/{id}").buildAndExpand(reportDto.getId()).toUri();
        return ResponseEntity.created(uri).body(reportDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable(name = "id") UUID id,
            @Valid
            @RequestBody UpdateReportRequest request, UriComponentsBuilder uriBuilder) {
        var reportDto = reportService.updateReport(id, request);
        var uri = uriBuilder.path("/reports/{id}").buildAndExpand(reportDto.getId()).toUri();
        return ResponseEntity.created(uri).body(reportDto);
    }

    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable UUID id) {
        reportService.deleteReport(id);
    }

    @ExceptionHandler(DuplicateReportException.class)
    public ResponseEntity<ErrorDto> handleDuplicateRequirementException(DuplicateReportException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ErrorDto> handleRequirementNotFound(ReportNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }
}
