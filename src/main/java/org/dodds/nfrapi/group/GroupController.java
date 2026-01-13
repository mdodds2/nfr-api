package org.dodds.nfrapi.group;

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
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public List<GroupDto> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{groupId}")
    public GroupDto getGroup(@PathVariable UUID groupId) {
        return groupService.getGroup(groupId);
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody CreateGroupRequest request, UriComponentsBuilder uriBuilder) {
        var groupDto = groupService.createGroup(request);
        var uri = uriBuilder.path("/groups/{id}").buildAndExpand(groupDto.getId()).toUri();
        return ResponseEntity.created(uri).body(groupDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable(name = "id") UUID id,
            @RequestBody UpdateGroupRequest request) {
        var groupDto = groupService.updateGroup(id , request);
        return ResponseEntity.ok().body(groupDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DuplicateGroupException.class)
    public ResponseEntity<ErrorDto> handleUniquenessError(DuplicateGroupException ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ErrorDto> handleGroupNotFound(GroupNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

}
