package org.dodds.nfrapi.report;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ReportDto getReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        return reportService.getReport(id, userId);
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @Valid @RequestBody CreateReportRequest request,
            @AuthenticationPrincipal UUID userId, UriComponentsBuilder uriBuilder) {
        var reportDto = reportService.createReport(userId, request);
        var uri = uriBuilder.path("/reports/{id}").buildAndExpand(reportDto.getId()).toUri();
        return ResponseEntity.created(uri).body(reportDto);
    }

    @PutMapping("/{id}")
    public ReportDto updateReport(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReportRequest request,
            @AuthenticationPrincipal UUID userId) {
        return reportService.updateReport(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        reportService.deleteReport(id, userId);
        return ResponseEntity.noContent().build();
    }

}
