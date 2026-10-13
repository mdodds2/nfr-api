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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}