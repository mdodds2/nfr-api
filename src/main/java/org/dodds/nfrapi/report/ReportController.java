package org.dodds.nfrapi.report;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.authentication.UserPrincipal;
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
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy ){
        return reportService.getAllReports(principal.userId(), sortBy);
    }

    @GetMapping("/{id}")
    public ReportDto getReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return reportService.getReport(id, userPrincipal.userId());
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @Valid @RequestBody CreateReportRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal, UriComponentsBuilder uriBuilder) {
        var reportDto = reportService.createReport(userPrincipal.userId(), request);
        var uri = uriBuilder.path("/reports/{id}").buildAndExpand(reportDto.getId()).toUri();
        return ResponseEntity.created(uri).body(reportDto);
    }

    @PutMapping("/{id}")
    public ReportDto updateReport(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReportRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return reportService.updateReport(id, userPrincipal.userId(), request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        reportService.deleteReport(id, userPrincipal.userId());
        return ResponseEntity.noContent().build();
    }

}
