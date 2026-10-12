package org.dodds.nfrapi.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryMapper mapper;

    @InjectMocks
    CategoryService categoryService;

    @DisplayName("Get All Categories")
    @Test
    void shouldCreateAReport() {

        // Arrange
        SubCategory subCategory = SubCategory.builder().id(UUID.randomUUID()).build();

        Category category = Category.builder().id(UUID.randomUUID()).build();
        category.setSubCategories(List.of(subCategory));

        List<Category> categories = new ArrayList<Category>();
        categories.add(category);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<CategoryDto> categoryDtoList = categoryService.getAllCategories();

        // Assert
        assertNotNull(categoryDtoList);

    }
}