package org.dodds.nfrapi.requirement;

import lombok.RequiredArgsConstructor;
import org.dodds.nfrapi.group.GroupNotFoundException;
import org.dodds.nfrapi.group.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final GroupRepository groupRepository;
    private final RequirementMapper requirementMapper;

    public List<RequirementDto> getAllRequirements() {
        //return requirementRepository.findByActive(true);
        var requirements = requirementRepository.findByActive(true);
        var list = new ArrayList<RequirementDto>();
        requirements.forEach(requirement -> list.add(requirementMapper.toDto(requirement)));
        return list;
    }

    public RequirementDto getRequirement(UUID requirementId) {
        var requirement = requirementRepository.findById(requirementId).orElseThrow(RequirementNotFoundException::new);
        return requirementMapper.toDto(requirement);
    }

    public List<RequirementDto> getRequirementsByGroup(UUID groupId) {
        var group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        var requirements = requirementRepository.findByGroupId(groupId);
        var list = new ArrayList<RequirementDto>();
        requirements.forEach(requirement -> list.add(requirementMapper.toDto(requirement)));
        return list;
    }

    public RequirementDto createRequirement(CreateRequirementRequest request) {
        if(requirementRepository.existsByName(request.getName()))
            throw new DuplicateRequirementException();

        var requirement = requirementMapper.toEntityFromCreateRequirementRequest(request);
        requirement.setActive(true);

        var group = groupRepository.findById(request.getGroupId()).orElseThrow(GroupNotFoundException::new);
        requirement.setGroup(group);

        requirementRepository.saveAndFlush(requirement);
        return requirementMapper.toDto(requirement);
    }

    public RequirementDto updateRequirement(UUID id, UpdateRequirementRequest request) {
        var requirement = requirementRepository.findById(id).orElseThrow(RequirementNotFoundException::new);
        requirementMapper.update(request, requirement);
        if(request.getGroupId() != null) {
            var group = groupRepository.findById(request.getGroupId()).orElseThrow(GroupNotFoundException::new);
            requirement.setGroup(group);
        }
        requirementRepository.save(requirement);
        return requirementMapper.toDto(requirement);
    }

    public void deleteRequirement(UUID id) {
        var requirement = requirementRepository.findById(id).orElseThrow(RequirementNotFoundException::new);
        requirementRepository.deleteById(id);
    }

}
