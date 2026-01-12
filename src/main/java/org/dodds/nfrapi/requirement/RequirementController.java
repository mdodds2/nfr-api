package org.dodds.nfrapi.requirement;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.common.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/requirements")
public class RequirementController {
    private final RequirementService requirementService;

    @GetMapping
    public List<Requirement> getAllRequirements() {
        return requirementService.getAllRequirements();
    }

    @GetMapping("/{requirementId}")
    public RequirementDto getRequirement(@PathVariable UUID requirementId) {
        return requirementService.getRequirement(requirementId);
    }

    @ExceptionHandler(RequirementNoFoundException.class)
    public ResponseEntity<ErrorDto> handleGroupNotFound(RequirementNoFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }
}
