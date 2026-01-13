package org.dodds.nfrapi.requirement;

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
@RequestMapping("/requirements")
public class RequirementController {
    private final RequirementService requirementService;

    @GetMapping
    public List<RequirementDto> getAllRequirements() {
        return requirementService.getAllRequirements();
    }

    @GetMapping("/group/{groupId}")
    public List<RequirementDto> getAllRequirementsForGroup(@PathVariable UUID groupId) {
        return requirementService.getRequirementsByGroup(groupId);
    }

    @GetMapping("/{id}")
    public RequirementDto getRequirement(@PathVariable UUID id) {
        return requirementService.getRequirement(id);
    }

    @PostMapping
    public ResponseEntity<RequirementDto> createRequirement(@RequestBody CreateRequirementRequest request, UriComponentsBuilder uriBuilder) {
        var requirementDto = requirementService.createRequirement(request);
        var uri = uriBuilder.path("/requirements/{id}").buildAndExpand(requirementDto.getId()).toUri();
        return ResponseEntity.created(uri).body(requirementDto);
    }

    @PatchMapping("/{id}")
    public RequirementDto updateRequirement(
            @PathVariable(name = "id") UUID id,
            @RequestBody UpdateRequirementRequest request) {
        return requirementService.updateRequirement(id , request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequirement(@PathVariable UUID id) {
        requirementService.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DuplicateRequirementException.class)
    public ResponseEntity<ErrorDto> handleDuplicateRequirementException(DuplicateRequirementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(RequirementNotFoundException.class)
    public ResponseEntity<ErrorDto> handleRequirementNotFound(RequirementNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

}
