package org.dodds.nfrapi.report;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.requirement.*;
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

    public Iterable<ReportDto> getAllReports(String sortBy) {

        if (!Set.of("name", "created_at").contains(sortBy))
            sortBy = "name";

        List<ReportDto> reportDtos = new ArrayList<>();
        List<Report> reports = reportRepository.findAll(Sort.by(sortBy));
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

/*
        return reportRepository.findAll(Sort.by(sortBy))
                .stream()
                .map(reportMapper::toDto)
                .toList();
*/
    }

    public ReportDto getReport(UUID id) {
        var report = reportRepository.findById(id).orElseThrow(ReportNotFoundException::new);
        var reportDto = reportMapper.toDto(report);

        var requirementDtos = new ArrayList<RequirementDto>();
        //for(Requirement requirement : report.getReportRequirements()) {
        report.getReportRequirements().forEach( requirement -> {
            requirementDtos.add(requirementMapper.toDto(requirement));
        });
        reportDto.setRequirements(requirementDtos);

        var measurementDtos = new ArrayList<MeasurementDto>();
        report.getMeasurements().forEach(measurement -> {
            measurementDtos.add(measurementMapper.toDto(measurement));
        });
        reportDto.setMeasurements(measurementDtos);

        return reportDto;
    }

    public Iterable<ReportDto> getReportsForUser(UUID id, String sortBy) {

        if (!Set.of("name", "createdAt").contains(sortBy))
            sortBy = "name";

        return reportRepository.findAllByUserId(id, Sort.by(sortBy))
                .stream()
                .map(reportMapper::toDto)
                .toList();
    }

    @Transactional
    public ReportDto createReport(CreateReportRequest request) {
        if(reportRepository.existsByUserIdAndName(request.getUserId(), request.getName())) {
            throw new DuplicateReportException();
        }
        var report = reportMapper.toEntity(request);

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

        reportRepository.flush();;

        var reportDto = reportMapper.toDto(report);
        List<RequirementDto> requirementDtos = new ArrayList<>();
        requirementList.forEach(requirement -> {
            requirementDtos.add(requirementMapper.toDto(requirement));
        });
        reportDto.setRequirements(requirementDtos);
        return reportDto;
    }

    public ReportDto updateReport(UUID reportId, UpdateReportRequest request) {
        var report = reportRepository.findById(reportId).orElseThrow(ReportNotFoundException::new);
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

            System.out.println("Mike:" + measure.getId());
        });
        report.setMeasurements(measurementSet);

        reportRepository.save(report);
        return reportMapper.toDto(report);
    }

    public void deleteReport(UUID id) {
        var report = reportRepository.findById(id).orElseThrow(ReportNotFoundException::new);
        reportRepository.delete(report);
    }

}
