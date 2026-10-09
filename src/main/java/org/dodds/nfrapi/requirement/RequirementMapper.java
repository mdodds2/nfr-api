package org.dodds.nfrapi.requirement;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RequirementMapper {

    @Mapping(target = "subCategoryId", source="subCategory.id")
    //@Mapping(target = "reports", ignore = true)
    RequirementDto toDto(Requirement requirement);

    @Mapping(source = "subCategoryId", target="subCategory.id")
    @Mapping(target = "reports", ignore = true)
    Requirement toEntity(RequirementDto requirementDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    @Mapping(source = "subCategoryId", target="subCategory.id")
    Requirement toEntityFromCreateRequirementRequest(CreateRequirementRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    @Mapping(target = "reports", ignore = true)
    Requirement toEntityFromUpdateRequirementRequest(UpdateRequirementRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    @Mapping(target = "createdMoment", ignore = true)
    @Mapping(target = "updatedMoment", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateRequirementRequest request, @MappingTarget Requirement requirement);
}