package org.dodds.nfrapi.requirement;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.category.CategoryNotFoundException;
import org.dodds.nfrapi.category.SubCategoryNotFoundException;
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

    @GetMapping("/subCategory/{subCategoryId}")
    public List<RequirementDto> getAllRequirementsForSubCategory(
            @PathVariable UUID subCategoryId,
            @RequestParam(required = false, defaultValue = "identifier", name = "sort") String sortBy) {
        return requirementService.getRequirementsBySubCategory(subCategoryId, sortBy);
    }

    @GetMapping("/{id}")
    public RequirementDto getRequirement(@PathVariable UUID id) {
        return requirementService.getRequirement(id);
    }

    @GetMapping("/nextIdentifier/{categoryId}/{subCategoryId}")
    public NextIdentifierDto getNextIdentifier(@PathVariable UUID categoryId, @PathVariable UUID subCategoryId) {
        return requirementService.getNextIdentifier(categoryId, subCategoryId);
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

}
