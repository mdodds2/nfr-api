package org.dodds.nfrapi.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories() {
        var categories = categoryRepository.findAll();
        var list = new ArrayList<CategoryDto>();
        categories.forEach(category -> {
            var categoryDto = categoryMapper.toDto(category);
            list.add(categoryDto);

            var subCategoryList = new ArrayList<SubCategoryDto>();
            category.getSubCategories().forEach( subCategory -> subCategoryList.add(categoryMapper.toDto(subCategory)));
            categoryDto.setSubCategories(subCategoryList);
        });

        return list;
    }

    public CategoryDto getCategory(UUID id) {
        var category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);

        var list = new ArrayList<SubCategoryDto>();
        category.getSubCategories().forEach( subCategory -> list.add(categoryMapper.toDto(subCategory)));

        var categoryDto = categoryMapper.toDto(category);
        categoryDto.setSubCategories(list);
        return categoryDto;
    }

    public CategoryDto createCategory(CreateCategoryRequest request) {
        if(categoryRepository.existsByName(request.getName())) {
            throw new DuplicateCategoryException();
        }
        Category category = categoryMapper.toEntityFromCreateCategoryRequest(request);
        category.setActive(true);
        categoryRepository.saveAndFlush(category);
        return categoryMapper.toDto(category);
    }

    public CategoryDto updateCategory(UUID id, UpdateCategoryRequest request) {
        var category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryMapper.update(request, category);
        categoryRepository.saveAndFlush(category);
        return categoryMapper.toDto(category);
    }

    public void deleteCategory(UUID id) {
        var category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(category);
    }

    public List<SubCategoryDto> getSubCategories(UUID id, String sortBy) {
        var sort = !Set.of("name", "sortOrder").contains(sortBy) ? "sortOrder" : sortBy;
        var subCategories = subCategoryRepository.findAllByCategoryId(id, Sort.by(sort));
        var list = new ArrayList<SubCategoryDto>();
        subCategories.forEach(subCategory -> list.add(categoryMapper.toDto(subCategory)));
        return list;
    }

    public SubCategoryDto getSubCategory(UUID id, UUID categoryId) {
        var subCategory = subCategoryRepository.findByIdAndCategoryId(id, categoryId);
        if(subCategory == null)
            throw new SubCategoryNotFoundException();
        return categoryMapper.toDto(subCategory);
    }

    public SubCategoryDto createSubCategory(UUID id, CreateSubCategoryRequest request) {
        var category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        var subCategory = categoryMapper.toEntityFromCreateSubCategoryRequest(request);
        //subCategory.setCategoryId(id);
        subCategory.setActive(true);
        subCategoryRepository.saveAndFlush(subCategory);
        return categoryMapper.toDto(subCategory);
    }

    public SubCategoryDto updateSubCategory(UUID id, UUID categoryId, UpdateSubCategoryRequest request) {
        var category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        var subCategory = subCategoryRepository.findByIdAndCategoryId(id, categoryId);
        if(subCategory == null)
            throw new SubCategoryNotFoundException();
        categoryMapper.update(request, subCategory);
        subCategoryRepository.saveAndFlush(subCategory);
        return categoryMapper.toDto(subCategory);
    }

    public void deleteSubCategory(UUID id, UUID categoryId) {
        var category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        var subCategory = subCategoryRepository.findByIdAndCategoryId(id, categoryId);
        if(subCategory == null) throw new SubCategoryNotFoundException();
        subCategoryRepository.delete(subCategory);
    }
}
