package org.dodds.nfrapi.category;

import org.dodds.nfrapi.authentication.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.endsWith;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip JWT/security filters for now
class CategoryControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean CategoryService categoryService;
    @MockitoBean JwtService jwtService;   // JwtAuthenticationFilter is a @Component and needs it

    @Test
    void shouldReturnCategory() throws Exception {
        var id = UUID.randomUUID();
        var dto = CategoryDto.builder().id(id).name("Performance").build();
        when(categoryService.getCategory(id)).thenReturn(dto);

        mockMvc.perform(get("/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Performance"));
    }

    @Test
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        var id = UUID.randomUUID();
        when(categoryService.getCategory(id)).thenThrow(new CategoryNotFoundException());

        mockMvc.perform(get("/categories/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenSubCategoryIsDuplicate() throws Exception {
        var id = UUID.randomUUID();
        var request = CreateSubCategoryRequest.builder().name("name").build();
        when(categoryService.createSubCategory(eq(id), any())).thenThrow(new DuplicateSubCategoryException());

        mockMvc.perform(post("/categories/{id}/subcategories", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldCreateCategoryAndReturnLocation() throws Exception {
        var id = UUID.randomUUID();
        var request = CreateCategoryRequest.builder().name("Performance").build();
        var dto = CategoryDto.builder().id(id).name("Performance").build();
        when(categoryService.createCategory(any())).thenReturn(dto);
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/categories/" + id)))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void shouldCreateSubCategoryAndReturnLocation() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        var request = CreateSubCategoryRequest.builder().name("Performance Sub Category").build();
        var dto = SubCategoryDto.builder().id(subCategoryId).categoryId(categoryId).name("Performance Sub Category").build();

        when(categoryService.createSubCategory(eq(categoryId), any())).thenReturn(dto);

        mockMvc.perform(post("/categories/{categoryId}/subcategories", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/categories/" + categoryId + "/subcategories/" + subCategoryId)))
                .andExpect(jsonPath("$.id").value(subCategoryId.toString()));
    }

}