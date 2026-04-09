package org.dodds.nfrapi.category;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.common.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("categories")
public class CategoryController {

    public final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryDto getCategory(@PathVariable UUID id) {
        return categoryService.getCategory(id);
    }

    @GetMapping("/{id}/subcategories")
    public List<SubCategoryDto> getSubCategories(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "sortOrder", name = "sort") String sortBy)  {
        return categoryService.getSubCategories(id, sortBy);
    }

    @GetMapping("/{categoryId}/subcategories/{id}")
    public SubCategoryDto getSubCategory(@PathVariable UUID categoryId, @PathVariable UUID id) {
        return categoryService.getSubCategory(id, categoryId);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CreateCategoryRequest request, UriComponentsBuilder uriBuilder) {
        var categoryDto = categoryService.createCategory(request);
        var uri = uriBuilder.path("/groups/{id}").buildAndExpand(categoryDto.getId()).toUri();
        return ResponseEntity.created(uri).body(categoryDto);
    }

    @PostMapping("/{id}/subcategories")
    public ResponseEntity<SubCategoryDto> createSubCategory(@PathVariable UUID id, @RequestBody CreateSubCategoryRequest request, UriComponentsBuilder uriBuilder) {
        var categoryDto = categoryService.createSubCategory(id, request);
        var uri = uriBuilder.path("/groups/{id}").buildAndExpand(categoryDto.getId()).toUri();
        return ResponseEntity.created(uri).body(categoryDto);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable UUID id, @RequestBody UpdateCategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @PatchMapping("/{categoryId}/subcategories/{id}")
    public SubCategoryDto updateSubCategory(@PathVariable UUID categoryId, @PathVariable UUID id, @RequestBody UpdateSubCategoryRequest request) {
        return categoryService.updateSubCategory(id, categoryId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{categoryId}/subcategories/{id}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable UUID categoryId, @PathVariable UUID id) {
        categoryService.deleteSubCategory(id, categoryId);
        return ResponseEntity.noContent().build();
    }



    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<ErrorDto> handleGroupNotFound(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

}
