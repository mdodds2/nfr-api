package org.dodds.nfrapi.users;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.report.ReportDto;
import org.dodds.nfrapi.report.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "Users controller description")
public class UserController {

    private final UserService userService;
    private final ReportService reportService;


    @GetMapping
    public Iterable<UserDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy){
        return userService.getAllUsers(sortBy);
    }

    @GetMapping("/me")
    public UserDto getUser(@AuthenticationPrincipal UUID userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/me/reports")
    public Iterable<ReportDto> getReportsForUser(
            @RequestParam(required = false, defaultValue = "createdAt", name = "sort") String sortBy,
            @AuthenticationPrincipal UUID userId) {
        return reportService.getReportsForUser(userId, sortBy);
    }

    @PostMapping
    public ResponseEntity<?> registerUser(
            @Valid
            @RequestBody RegisterUserRequest request, UriComponentsBuilder uriBuilder) {
        var userDto = userService.registerUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/me")
    public UserDto updateUser(
            @Valid @RequestBody UpdateUserRequest request, @AuthenticationPrincipal UUID userId) {

        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePasswordWhenLoggedIn(
            @Valid @RequestBody ChangePasswordRequest request, @AuthenticationPrincipal UUID userId) {
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

}
