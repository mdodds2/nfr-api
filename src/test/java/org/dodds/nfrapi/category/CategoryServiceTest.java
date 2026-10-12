package org.dodds.nfrapi.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryMapper mapper;

    @Mock
    SubCategoryRepository subCategoryRepository;

    @InjectMocks
    CategoryService categoryService;

    @DisplayName("Get All Categories")
    @Test
    void shouldReturnAllCategoriesWithSubCategories() {

        // Arrange
        SubCategory subCategory = SubCategory.builder().id(UUID.randomUUID()).build();
        Category category = Category.builder().id(UUID.randomUUID()).build();
        category.setSubCategories(List.of(subCategory));

        CategoryDto categoryDto = CategoryDto.builder().id(category.getId()).build();
        SubCategoryDto subCategoryDto = SubCategoryDto.builder().id(UUID.randomUUID()).build();

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(mapper.toDto(category)).thenReturn(categoryDto);
        when(mapper.toDto(subCategory)).thenReturn(subCategoryDto);

        // Act
        List<CategoryDto> result = categoryService.getAllCategories();

        // Assert
        assertThat(result).containsExactly(categoryDto);
        assertThat(result.get(0).getSubCategories()).containsExactly(subCategoryDto);
    }

    @DisplayName("Return a Category - category found")
    @Test
    void shouldReturnCategoryWhenIdExists() {
        //Arrange
        SubCategory subCategory = SubCategory.builder().id(UUID.randomUUID()).build();
        Category category = Category.builder().id(UUID.randomUUID()).build();
        category.setSubCategories(List.of(subCategory));
        CategoryDto categoryDto = CategoryDto.builder().id(category.getId()).build();
        SubCategoryDto subCategoryDto = SubCategoryDto.builder().id(UUID.randomUUID()).build();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(mapper.toDto(category)).thenReturn(categoryDto);
        when(mapper.toDto(subCategory)).thenReturn(subCategoryDto);

        // Act
        CategoryDto result = categoryService.getCategory(category.getId());

        // Assert
        assertThat(result).isEqualTo(categoryDto);
        assertThat(result.getSubCategories()).containsExactly(subCategoryDto);
    }

    @DisplayName("Return a Category - category not found")
    @Test
    void shouldThrowCategoryNotFoundWhenIdDoesNotExist() {
        //Arrange
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategory(id));
    }

    @DisplayName("Create a category")
    @Test
    void shouldCreateACategory() {
        // Arrange
        var request = CreateCategoryRequest.builder()
                .name("category name")
                .shortName("short name")
                .description("category description")
                .sortOrder(1)
                .image("")
                .build();

        var category = Category.builder().id(UUID.randomUUID()).build();
        var categoryDto = CategoryDto.builder().id(category.getId()).build();

        when(categoryRepository.existsByName(request.name())).thenReturn(false);
        when(mapper.toEntityFromCreateCategoryRequest(request)).thenReturn(category);
        when(mapper.toDto(category)).thenReturn(categoryDto);
        // Act
        var result = categoryService.createCategory(request);

        // Assert
        assertThat(result).isEqualTo(categoryDto);
        assertThat(category.getActive()).isTrue();
        verify(categoryRepository).saveAndFlush(category);

    }

    @DisplayName("Create a category with a duplicate name")
    @Test
    void shouldNotCreateACategoryBecauseOfADuplicateName() {
        // Arrange
        var request = CreateCategoryRequest.builder()
                .name("category name")
                .shortName("short name")
                .description("category description")
                .sortOrder(1)
                .image("")
                .build();

        when(categoryRepository.existsByName(request.name())).thenReturn(true);

        // Act

        // Assert
        assertThrows(DuplicateCategoryException.class, () -> categoryService.createCategory(request));
        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @DisplayName("Should update a category")
    @Test
    void shouldUpdateACategory() {
        // Arrange
        var request = UpdateCategoryRequest.builder()
                .name("category name")
                .shortName("short name")
                .description("category description")
                .sortOrder(1)
                .build();

        var id = UUID.randomUUID();
        var category = Category.builder().id(id).build();
        var categoryDto = CategoryDto.builder().id(id).build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(mapper.toDto(category)).thenReturn(categoryDto);

        // Act
        var result = categoryService.updateCategory(id, request);

        // Assert
        assertThat(result).isEqualTo(categoryDto);
        verify(mapper).update(request, category);
        verify(categoryRepository).saveAndFlush(category);
    }

    @DisplayName("Should not update a category due to category not found")
    @Test
    void shouldNotUpdateACategoryDueToCategoryNotFound() {
        // Arrange
        var request = UpdateCategoryRequest.builder()
                .name("category name")
                .shortName("short name")
                .description("category description")
                .sortOrder(1)
                .build();
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(id, request));
        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @DisplayName("should delete a category")
    @Test
    void shouldDeleteACategory() {
        // Arrange
        var id = UUID.randomUUID();
        var category = Category.builder().id(id).build();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // Act
        categoryService.deleteCategory(id);

        // Assert
        verify(categoryRepository).delete(category);
    }

    @DisplayName("should not delete a category due to category not found")
    @Test
    void shouldNotDeleteACategoryDueToCategoryNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(id));
        verify(categoryRepository, never()).delete(any());
    }

    @DisplayName("Get SubCategories with no sort order")
    @Test
    void shouldReturnAllSubcategoriesWithoutSpecifyingSortOrder() {
        // Arrange
        var id = UUID.randomUUID();
        var subCategory = SubCategory.builder().id(UUID.randomUUID()).build();
        var subCategoryDto = SubCategoryDto.builder().id(subCategory.getId()).build();

        when(subCategoryRepository.findAllByCategoryId(id, Sort.by("sortOrder"))).thenReturn(List.of(subCategory));
        when(mapper.toDto(subCategory)).thenReturn(subCategoryDto);

        // Act
        var result = categoryService.getSubCategories(id, null);

        // Assert
        assertThat(result).containsExactly(subCategoryDto);
    }

    @DisplayName("Get SubCategories sorted by name")
    @Test
    void shouldReturnAllSubcategoriesWithSpecifyingSortOrder() {

        // Arrange
        var id = UUID.randomUUID();
        var subCategory = SubCategory.builder().id(UUID.randomUUID()).name("subCategory name").build();
        var subCategoryDto = SubCategoryDto.builder().id(subCategory.getId()).build();

        when(subCategoryRepository.findAllByCategoryId(id, Sort.by("name"))).thenReturn(List.of(subCategory));
        when(mapper.toDto(subCategory)).thenReturn(subCategoryDto);

        // Act
        var result = categoryService.getSubCategories(id, "name");

        // Assert
        assertThat(result).containsExactly(subCategoryDto);
    }
}