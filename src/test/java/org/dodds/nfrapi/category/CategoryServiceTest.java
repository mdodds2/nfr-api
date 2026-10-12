package org.dodds.nfrapi.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void shouldThrowAnExceptionWhenIdDoesNotExist() {
        //Arrange
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategory(id));
    }

}