package org.dodds.nfrapi.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dodds.nfrapi.authentication.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip JWT/security filters for now
public class ReportControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReportService reportService;

    @MockitoBean
    JwtService jwtService;   // JwtAuthenticationFilter is a @Component and needs it

    @Test
    void shouldGetAllReports() throws Exception {

        var report1 = ReportDto.builder().id(UUID.randomUUID()).name("Report 1").build();
        var report2 = ReportDto.builder().id(UUID.randomUUID()).name("Report 2").build();
        var report3 = ReportDto.builder().id(UUID.randomUUID()).name("Report 3").build();

        when(reportService.getAllReports(any())).thenReturn(List.of(report1, report2, report3));

        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(report1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(report2.getId().toString()))
                .andExpect(jsonPath("$[2].id").value(report3.getId().toString()));
    }

    @Test
    void shouldGetAReport() throws Exception {
        var reportId = UUID.randomUUID();
        var reportDto = ReportDto.builder().id(reportId).name("Report 1").build();

        when(reportService.getReport(reportId)).thenReturn(reportDto);

        mockMvc.perform((get("/reports/{id}", reportId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reportId.toString()));
    }

    @Test
    void shouldNotGetAReportNotFound() throws Exception {
        var reportId = UUID.randomUUID();

        when(reportService.getReport(reportId)).thenThrow(new ReportNotFoundException());

        mockMvc.perform((get("/reports/{id}", reportId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAReport() throws Exception {
        var request = CreateReportRequest.builder()
                        .userId(UUID.randomUUID())
                        .name("report1")
                        .description("some description")
                        .build();

        var id = UUID.randomUUID();
        var dto = ReportDto.builder()
                        .id(id)
                        .name("report1")
                        .description("some description")
                        .build();

        when(reportService.createReport(request)).thenReturn(dto);

        mockMvc.perform((post("/reports")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/reports/" + id)));
    }

    @Test
    void shouldNotCreateAReportDuplicate() throws Exception {
        var request = CreateReportRequest.builder()
                .userId(UUID.randomUUID())
                .name("report1")
                .description("some description")
                .build();

        when(reportService.createReport(request)).thenThrow(new DuplicateReportException());

        mockMvc.perform((post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldUpdateAReport() throws Exception {
        var userId = UUID.randomUUID();
        var request = UpdateReportRequest.builder()
                .userId(userId)
                .name("report1")
                .description("some description changed")
                .build();

        var reportId = UUID.randomUUID();
        var dto = ReportDto.builder()
                .id(reportId)
                .userId(userId)
                .name("report1")
                .description("some description changed")
                .build();

        when(reportService.updateReport(reportId, request)).thenReturn(dto);

        mockMvc.perform(put("/reports/{id}", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reportId.toString()));;
    }

    @Test
    void shouldNotUpdateAReportNotFound() throws Exception {
        var userId = UUID.randomUUID();
        var request = UpdateReportRequest.builder()
                .userId(userId)
                .name("report1")
                .description("some description changed")
                .build();

        var reportId = UUID.randomUUID();

        when(reportService.updateReport(reportId, request)).thenThrow(new ReportNotFoundException());

        mockMvc.perform(put("/reports/{id}", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteReport() throws Exception {
        var reportId = UUID.randomUUID();
        doNothing().when(reportService).deleteReport(reportId);
        mockMvc.perform(delete("/reports/{id}", reportId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteReportNotFound() throws Exception {
        var reportId = UUID.randomUUID();
        doThrow(new ReportNotFoundException()).when(reportService).deleteReport(reportId);
        mockMvc.perform(delete("/reports/{id}", reportId))
                .andExpect(status().isNotFound());
    }

}
