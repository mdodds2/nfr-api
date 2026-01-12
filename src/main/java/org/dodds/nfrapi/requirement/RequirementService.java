package org.dodds.nfrapi.requirement;

import lombok.RequiredArgsConstructor;
import org.dodds.nfrapi.group.Group;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final RequirementMapper requirementMapper;

    public List<Requirement> getAllRequirements() {
        return requirementRepository.findByActive(true);
    }

    public RequirementDto getRequirement(UUID requirementId) {
        var requirement = requirementRepository.findById(requirementId).orElseThrow(RequirementNoFoundException::new);
        return requirementMapper.toDto(requirement);
    }
}
