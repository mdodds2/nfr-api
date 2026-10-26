package org.dodds.nfrapi.report;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.requirement.*;
import org.dodds.nfrapi.users.User;
import org.dodds.nfrapi.users.UserNotFoundException;
import org.dodds.nfrapi.users.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@AllArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final MeasurementMapper measurementMapper;
    private final RequirementRepository requirementRepository;
    private final RequirementMapper requirementMapper;
    private final UserRepository userRepository;

    public List<ReportDto> getAllReports(UUID userId, String sortBy) {

        if (sortBy == null || !Set.of("name", "createdAt").contains(sortBy))
            sortBy = "name";

        List<Report> reports = reportRepository.findAllByUserId(userId, Sort.by(sortBy));

        List<ReportDto> reportDtos = new ArrayList<>();

        reports.forEach( report -> {
            ReportDto dto = reportMapper.toDto(report);

            ArrayList<RequirementDto> requirementDtos = new ArrayList<>();
            report.getReportRequirements().forEach(requirement -> {
                RequirementDto requirementDto = requirementMapper.toDto(requirement);
                requirementDtos.add(requirementDto);
            });
            dto.setRequirements(requirementDtos);

            ArrayList<MeasurementDto> measurementDtos = new ArrayList<>();
            report.getMeasurements().forEach(measurement -> {
                MeasurementDto measurementDto = measurementMapper.toDto(measurement);
                measurementDtos.add(measurementDto);
            });
            dto.setMeasurements(measurementDtos);

            reportDtos.add(dto);
        });

        return reportDtos;
    }

    public ReportDto getReport(UUID id, UUID userId) {
        var report = reportRepository.findById(id)
                .filter(r -> r.getUserId().equals(userId))
                .orElseThrow(ReportNotFoundException::new);
        var reportDto = toDtoWithOwner(report);

        var requirementDtos = report.getReportRequirements().stream()
                .map(requirementMapper::toDto)
                .toList();

        reportDto.setRequirements(requirementDtos);

        var measurementDtos = new ArrayList<MeasurementDto>();
        report.getMeasurements().forEach(measurement -> {
            measurementDtos.add(measurementMapper.toDto(measurement));
        });
        reportDto.setMeasurements(measurementDtos);

        return reportDto;
    }

    public List<ReportDto> getReportsForUser(UUID id, String sortBy) {

        if (sortBy == null || !Set.of("name", "createdAt").contains(sortBy))
            sortBy = "name";

        String userName = userRepository.findById(id)
                .map(User::getName)
                .orElse(null);

        return reportRepository.findAllByUserId(id, Sort.by(sortBy))
                .stream()
                .map(r -> { var dto = reportMapper.toDto(r); dto.setUserName(userName); return dto; })
                .toList();
    }

    @Transactional
    public ReportDto createReport(UUID userId, CreateReportRequest request) {
        if(reportRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new DuplicateReportException();
        }
        var report = reportMapper.toEntity(request);

        report.setUserId(userId);

        List<Requirement> requirementList = new ArrayList<>();
        request.getRequirementIds().forEach((id) -> {
            Requirement req = requirementRepository.findById(id).orElseThrow(RequirementNotFoundException::new);
            requirementList.add(req);
        });
        report.setReportRequirements(requirementList);

        reportRepository.save(report);

        Set<Measurement> measurementSet = new HashSet<>();
        request.getMeasurements().forEach((measureDto) -> {
            Measurement measure = measurementMapper.toEntity(measureDto);
            measure.setReport(report);
            measurementSet.add(measure);
        });
        report.setMeasurements(measurementSet);

        reportRepository.flush();

        var reportDto = reportMapper.toDto(report);
        List<RequirementDto> requirementDtos = new ArrayList<>();
        requirementList.forEach(requirement -> {
            requirementDtos.add(requirementMapper.toDto(requirement));
        });
        reportDto.setRequirements(requirementDtos);
        return reportDto;
    }

    public ReportDto updateReport(UUID reportId, UUID userId, UpdateReportRequest request) {
        var report = reportRepository.findById(reportId)
            .filter(r -> r.getUserId().equals(userId))
            .orElseThrow(ReportNotFoundException::new);
        reportMapper.update(request, report);

        List<Requirement> requirementList = new ArrayList<>();
        request.getRequirementIds().forEach((id) -> {
            Requirement req = requirementRepository.findById(id).orElseThrow(RequirementNotFoundException::new);
            requirementList.add(req);
        });
        report.setReportRequirements(requirementList);

        Set<Measurement> measurementSet = new HashSet<>();
        request.getMeasurements().forEach((measureDto) -> {
            Measurement measure = measurementMapper.toEntity(measureDto);
            measure.setReport(report);
            measurementSet.add(measure);
        });
        report.setMeasurements(measurementSet);

        reportRepository.save(report);
        return reportMapper.toDto(report);
    }

    public void deleteReport(UUID id, UUID userId) {
        var report = reportRepository.findById(id)
                .filter(r -> r.getUserId().equals(userId))
                .orElseThrow(ReportNotFoundException::new);
        reportRepository.delete(report);
    }

    private ReportDto toDtoWithOwner(Report report) {
        ReportDto dto = reportMapper.toDto(report);
        userRepository.findById(report.getUserId())
                .ifPresent(u -> dto.setUserName(u.getName()));
        return dto;
    }

}
