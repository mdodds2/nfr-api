package org.dodds.nfrapi.requirement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dodds.nfrapi.authentication.JwtService;
import org.dodds.nfrapi.category.CategoryNotFoundException;
import org.dodds.nfrapi.category.SubCategoryNotFoundException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequirementController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip JWT/security filters for now
public class RequirementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RequirementService requirementService;

    @MockitoBean
    JwtService jwtService;   // JwtAuthenticationFilter is a @Component and needs it

    @Test
    void shouldReturnAllRequirements() throws Exception {

        var req1 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 1").build();
        var req2 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 2").build();
        var req3 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 3").build();

        when(requirementService.getAllRequirements()).thenReturn(List.of(req1, req2, req3));

        mockMvc.perform(get("/requirements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(req1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(req2.getId().toString()))
                .andExpect(jsonPath("$[2].id").value(req3.getId().toString()));
    }

    @Test
    void shouldReturnRequirementsForSubCategory() throws Exception {

        var subCategoryId = UUID.randomUUID();
        var req1 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 1").build();
        var req2 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 2").build();
        var req3 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 3").build();

        when(requirementService.getRequirementsBySubCategory(subCategoryId, "identifier")).thenReturn(List.of(req1, req2, req3));

        mockMvc.perform(get("/requirements/subCategory/{subCategoryId}", subCategoryId).param("sort", "identifier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(req1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(req2.getId().toString()))
                .andExpect(jsonPath("$[2].id").value(req3.getId().toString()));
    }

    @Test
    void shouldNotReturnRequirementsForSubCategoryNotFound() throws Exception {

        var subCategoryId = UUID.randomUUID();
        var req1 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 1").build();
        var req2 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 2").build();
        var req3 = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 3").build();

        when(requirementService.getRequirementsBySubCategory(eq(subCategoryId), any())).thenThrow(new SubCategoryNotFoundException());

        mockMvc.perform(get("/requirements/subCategory/{subCategoryId}", subCategoryId).param("sort", "identifier"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnARequirement() throws Exception {
        var requirementId = UUID.randomUUID();
        var req = RequirementDto.builder().id(requirementId).title("Requirement 1").build();

        when(requirementService.getRequirement(requirementId)).thenReturn(req);

        mockMvc.perform(get("/requirements/{requirementId}", requirementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(req.getId().toString()));
    }

    @Test
    void shouldNotReturnARequirementNotFound() throws Exception {
        var requirementId = UUID.randomUUID();
        when(requirementService.getRequirement(requirementId)).thenThrow(new RequirementNotFoundException());
        mockMvc.perform(get("/requirements/{requirementId}", requirementId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetNextIdentifier() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        var nextId = UUID.randomUUID();
        var dto = NextIdentifierDto.builder().identifier(nextId.toString()).build();

        when(requirementService.getNextIdentifier(any(), any())).thenReturn(dto);

        mockMvc.perform(get("/requirements/nextIdentifier/{categoryId}/{subCategoryId}", categoryId, subCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value(nextId.toString()));
    }

    @Test
    void shouldNotGetNextIdentifierCategoryNotFound() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        when(requirementService.getNextIdentifier(any(), any())).thenThrow(new CategoryNotFoundException());
        mockMvc.perform(get("/requirements/nextIdentifier/{categoryId}/{subCategoryId}", categoryId, subCategoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetNextIdentifierSubcategoryNotFound() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        when(requirementService.getNextIdentifier(any(), any())).thenThrow(new SubCategoryNotFoundException());
        mockMvc.perform(get("/requirements/nextIdentifier/{categoryId}/{subCategoryId}", categoryId, subCategoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateRequirement() throws Exception {
        var request = CreateRequirementRequest.builder()
                .title("requirement 1")
                .description("Some Description")
                .build();
        var id = UUID.randomUUID();
        var dto = RequirementDto.builder().id(id).title("requirement 1").description("Some Description").build();

        when(requirementService.createRequirement(any(CreateRequirementRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(header().string("Location", endsWith("/requirements/" + id)));
    }

    @Test
    void shouldNotCreateRequirementDuplicateIdentifier() throws Exception {
        var request = CreateRequirementRequest.builder()
                .title("requirement 1")
                .description("Some Description")
                .identifier("some identifier")
                .build();

        when(requirementService.createRequirement(any(CreateRequirementRequest.class))).thenThrow(new DuplicateRequirementException());

        mockMvc.perform(post("/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdateRequirement() throws Exception {
        var id = UUID.randomUUID();
        var request = UpdateRequirementRequest.builder()
                .title("requirement 1")
                .description("Some Description")
                .build();
        var dto = RequirementDto.builder().id(id).title("requirement 1").description("Some Description").build();

        when(requirementService.updateRequirement(eq(id), any())).thenReturn(dto);

        mockMvc.perform(patch("/requirements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void shouldNotUpdateRequirementNotFound() throws Exception {
        var id = UUID.randomUUID();
        var request = UpdateRequirementRequest.builder()
                .title("requirement 1")
                .description("Some Description")
                .build();

        when(requirementService.updateRequirement(eq(id), any())).thenThrow(new RequirementNotFoundException());

        mockMvc.perform(patch("/requirements/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteRequirement() throws Exception {
        var id = UUID.randomUUID();

        mockMvc.perform(delete("/requirements/{id}", id))
                .andExpect(status().isNoContent());

        verify(requirementService).deleteRequirement(id);
    }

    @Test
    void shouldNotDeleteRequirement() throws Exception {
        var id = UUID.randomUUID();

        doThrow(new RequirementNotFoundException()).when(requirementService).deleteRequirement(id);

        mockMvc.perform(delete("/requirements/{id}", id))
                .andExpect(status().isNotFound());
    }

}
