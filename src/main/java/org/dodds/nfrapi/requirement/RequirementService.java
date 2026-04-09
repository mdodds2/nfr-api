package org.dodds.nfrapi.requirement;

import lombok.RequiredArgsConstructor;
import org.dodds.nfrapi.category.CategoryNotFoundException;
import org.dodds.nfrapi.category.CategoryRepository;
import org.dodds.nfrapi.category.SubCategoryNotFoundException;
import org.dodds.nfrapi.category.SubCategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final RequirementMapper requirementMapper;

    public List<RequirementDto> getAllRequirements() {
        var requirements = requirementRepository.findAll();
        var list = new ArrayList<RequirementDto>();
        requirements.forEach(requirement -> list.add(requirementMapper.toDto(requirement)));
        return list;
    }

    public RequirementDto getRequirement(UUID requirementId) {
        var requirement = requirementRepository.findById(requirementId).orElseThrow(RequirementNotFoundException::new);
        return requirementMapper.toDto(requirement);
    }

    public List<RequirementDto> getRequirementsBySubCategory(UUID subCategoryId, String sortBy) {

        if (!Set.of("identifier", "title", "created_at").contains(sortBy)) {
            sortBy = "identifier";
        }

        var group = subCategoryRepository.findById(subCategoryId).orElseThrow(SubCategoryNotFoundException::new);
        var requirements = requirementRepository.findBySubCategoryId(subCategoryId, Sort.by(sortBy));
        var list = new ArrayList<RequirementDto>();
        requirements.forEach(requirement -> list.add(requirementMapper.toDto(requirement)));
        return list;
    }

    public RequirementDto createRequirement(CreateRequirementRequest request) {
        if(requirementRepository.existsByIdentifier(request.getIdentifier()))
            throw new DuplicateRequirementException();

        var requirement = requirementMapper.toEntityFromCreateRequirementRequest(request);
        var subCategory = subCategoryRepository.findById(request.getSubCategoryId()).orElseThrow(SubCategoryNotFoundException::new);
        requirement.setSubCategory(subCategory);

        requirementRepository.saveAndFlush(requirement);
        return requirementMapper.toDto(requirement);
    }

    public RequirementDto updateRequirement(UUID id, UpdateRequirementRequest request) {
        var requirement = requirementRepository.findById(id).orElseThrow(RequirementNotFoundException::new);
        requirementMapper.update(request, requirement);
//        if(request.getGroupId() != null) {
//            var group = groupRepository.findById(request.getGroupId()).orElseThrow(GroupNotFoundException::new);
//            requirement.setGroup(group);
//        }
        requirementRepository.save(requirement);
        return requirementMapper.toDto(requirement);
    }

    public void deleteRequirement(UUID id) {
        var requirement = requirementRepository.findById(id).orElseThrow(RequirementNotFoundException::new);
        requirementRepository.deleteById(id);
    }

    public NextIdentifierDto getNextIdentifier(UUID categoryId, UUID subCategoryId) {
        var category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        var subCategory = subCategoryRepository.findById(subCategoryId).orElseThrow(SubCategoryNotFoundException::new);
        var count = requirementRepository.countBySubCategoryId(subCategoryId);

        return new  NextIdentifierDto(category.getShortName() + '-' + subCategory.getShortName() + '-' + (count + 1));
    }

}
