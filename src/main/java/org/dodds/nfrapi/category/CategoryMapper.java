package org.dodds.nfrapi.category;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "subCategories", ignore = true)
    CategoryDto toDto(Category category);

    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntityFromCreateCategoryRequest(CreateCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateCategoryRequest request, @MappingTarget Category category);

    @Mapping(target = "categoryId", ignore = true)
    SubCategoryDto toDto(SubCategory subCategory);

    @Mapping(target = "category", ignore = true)
    SubCategory toEntity(SubCategoryDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    SubCategory toEntityFromCreateSubCategoryRequest(CreateSubCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateSubCategoryRequest request, @MappingTarget SubCategory category);

}
