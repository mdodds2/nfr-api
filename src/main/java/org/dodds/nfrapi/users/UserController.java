package org.dodds.nfrapi.users;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.report.ReportDto;
import org.dodds.nfrapi.report.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "Users controller description")
public class UserController {

    private final UserService userService;
    private final ReportService reportsService;


    @GetMapping
    public Iterable<UserDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy
    ){
        return userService.getAllUsers(sortBy);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/reports")
    public Iterable<ReportDto> getReportsForUser(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "createdAt", name = "sort") String sortBy) {
        return reportsService.getReportsForUser(id, sortBy);
    }

    @PostMapping
    public ResponseEntity<?> registerUser(
            @Valid
            @RequestBody RegisterUserRequest request, UriComponentsBuilder uriBuilder) {
        var userDto = userService.registerUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(
            @PathVariable(name = "id") UUID id,
            @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{id}/change-password")
    public void changePassword(
            @PathVariable(name = "id") UUID id,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateUser() {
        return ResponseEntity.badRequest().body(
                Map.of("message", "Email is already registered.")
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
