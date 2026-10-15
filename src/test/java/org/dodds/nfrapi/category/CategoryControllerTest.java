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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
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
    void shouldReturn409WhenCategoryIsDuplicate() throws Exception {
        var id = UUID.randomUUID();
        var request = CreateCategoryRequest.builder().name("name").build();
        when(categoryService.createCategory(any())).thenThrow(new DuplicateCategoryException());

        mockMvc.perform(post("/categories", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
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

    @Test
    void shouldReturnAllCategories() throws Exception {

        var category1 = CategoryDto.builder().id(UUID.randomUUID()).build();
        var category2 = CategoryDto.builder().id(UUID.randomUUID()).build();
        var list = List.of(category1, category2);
        when(categoryService.getAllCategories()).thenReturn(list);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(category1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(category2.getId().toString()));
    }

    @Test
    void shouldReturnAllSubCategoriesForACategoryByName() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategory1 = SubCategoryDto.builder().id(UUID.randomUUID()).name("SubC").build();
        var subCategory2 = SubCategoryDto.builder().id(UUID.randomUUID()).name("SubA").build();
        var subCategory3 = SubCategoryDto.builder().id(UUID.randomUUID()).name("SubB").build();
        var list = List.of(subCategory1, subCategory2, subCategory3);

        when(categoryService.getSubCategories(categoryId, "name")).thenReturn(list);

        mockMvc.perform(get("/categories/{categoryId}/subcategories", categoryId).param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(subCategory1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(subCategory2.getId().toString()))
                .andExpect(jsonPath("$[2].id").value(subCategory3.getId().toString()));
    }

    @Test
    void shouldReturnAllSubCategoriesForACategoryWithoutSort() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategory1 = SubCategoryDto.builder().id(UUID.randomUUID()).name("SubC").build();
        var subCategory2 = SubCategoryDto.builder().id(UUID.randomUUID()).name("SubA").build();
        var subCategory3 = SubCategoryDto.builder().id(UUID.randomUUID()).name("SubB").build();
        var list = List.of(subCategory1, subCategory2, subCategory3);

        when(categoryService.getSubCategories(categoryId, "none")).thenReturn(list);

        mockMvc.perform(get("/categories/{categoryId}/subcategories", categoryId).param("sort", "none"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(subCategory1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(subCategory2.getId().toString()))
                .andExpect(jsonPath("$[2].id").value(subCategory3.getId().toString()));
    }

    @Test
    void shouldReturnASubCategory() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        var dto = SubCategoryDto.builder().id(subCategoryId).categoryId(categoryId).build();

        when(categoryService.getSubCategory(subCategoryId, categoryId)).thenReturn(dto);

        mockMvc.perform(get("/categories/{categoryId}/subcategories/{subCategoryId}", categoryId, subCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId().toString()));
    }

    @Test
    void shouldNotReturnASubCategoryDueToCategoryNotFound() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        when(categoryService.getSubCategory(subCategoryId, categoryId)).thenThrow(new CategoryNotFoundException());

        mockMvc.perform(get("/categories/{categoryId}/subcategories/{subCategoryId}", categoryId, subCategoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotReturnASubCategoryDueToSubCategoryNotFound() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        when(categoryService.getSubCategory(subCategoryId, categoryId)).thenThrow(new SubCategoryNotFoundException());

        mockMvc.perform(get("/categories/{categoryId}/subcategories/{subCategoryId}", categoryId, subCategoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        var categoryId = UUID.randomUUID();
        var request = UpdateCategoryRequest.builder().name("Performance Updated").build();
        var dto = CategoryDto.builder().id(categoryId).name("Performance Updated").build();

        when(categoryService.updateCategory(eq(categoryId), any(UpdateCategoryRequest.class))).thenReturn(dto);

        mockMvc.perform(patch("/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId.toString()));
    }

    @Test
    void shouldNotUpdateCategoryDueToCategoryNotFoundException() throws Exception {
        var categoryId = UUID.randomUUID();
        var request = UpdateCategoryRequest.builder().name("Performance Updated").build();
        var dto = CategoryDto.builder().id(categoryId).name("Performance Updated").build();

        when(categoryService.updateCategory(eq(categoryId), any(UpdateCategoryRequest.class))).thenThrow(new CategoryNotFoundException());

        mockMvc.perform(patch("/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateSubCategory() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        var request = UpdateSubCategoryRequest.builder().name("Performance Updated").build();
        var dto = SubCategoryDto.builder().id(subCategoryId).categoryId(categoryId).name("Performance Updated").build();

        when(categoryService.updateSubCategory(eq(subCategoryId), eq(categoryId), any(UpdateSubCategoryRequest.class))).thenReturn(dto);

        mockMvc.perform(patch("/categories/{categoryId}/subcategories/{subCategoryId}", categoryId, subCategoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subCategoryId.toString()));
    }

    @Test
    void shouldNotUpdateSubCategoryDueToCategoryNotFoundException() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        var request = UpdateSubCategoryRequest.builder().name("Performance Updated").build();

        when(categoryService.updateSubCategory(eq(subCategoryId), eq(categoryId), any())).thenThrow(new CategoryNotFoundException());

        mockMvc.perform(patch("/categories/{categoryId}/subcategories/{subCategoryId}", categoryId, subCategoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category not found."));
    }

    @Test
    void shouldNotUpdateSubCategoryDueToSubCategoryNotFoundException() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();
        var request = UpdateSubCategoryRequest.builder().name("Performance Updated").build();

        when(categoryService.updateSubCategory(eq(subCategoryId), eq(categoryId), any())).thenThrow(new SubCategoryNotFoundException());

        mockMvc.perform(patch("/categories/{categoryId}/subcategories/{subCategoryId}", categoryId, subCategoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Subcategory not found."));;
    }

    @Test
    void shouldDeleteCategory() throws Exception {
        var categoryId = UUID.randomUUID();
        mockMvc.perform(delete("/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
)                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteCategoryAndThrowCategoryNotFoundException() throws Exception {
        var categoryId = UUID.randomUUID();

        doThrow(new CategoryNotFoundException()).when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteSubCategory() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();

        mockMvc.perform(delete("/categories/{categoryId}/subcategories/{subcategoryId}", categoryId, subCategoryId)
                .contentType(MediaType.APPLICATION_JSON))
                       .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteSubCategoryAndThrowCategoryNotFoundException() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();

        doThrow(new CategoryNotFoundException()).when(categoryService).deleteSubCategory(subCategoryId, categoryId);

        mockMvc.perform(delete("/categories/{categoryId}/subcategories/{subCategoryId}", categoryId, subCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
        )                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteSubCategoryAndThrowSubCategoryNotFoundException() throws Exception {
        var categoryId = UUID.randomUUID();
        var subCategoryId = UUID.randomUUID();

        doThrow(new SubCategoryNotFoundException()).when(categoryService).deleteSubCategory(subCategoryId, categoryId);

        mockMvc.perform(delete("/categories/{categoryId}/subcategories/{subCategoryId}", categoryId, subCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
        )                .andExpect(status().isNotFound());
    }


}