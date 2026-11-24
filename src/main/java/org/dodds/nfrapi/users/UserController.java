package org.dodds.nfrapi.users;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.authentication.UserPrincipal;
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

    @GetMapping
    public Iterable<UserDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy){
        return userService.getAllUsers(sortBy);
    }

    @GetMapping("/{id}")
    public UserDto getAUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @GetMapping("/me")
    public CurrentUserDto getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getCurrentUser(userPrincipal.userId());
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
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.updateUser(userPrincipal.userId(), request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePasswordWhenLoggedIn(
            @Valid @RequestBody ChangePasswordRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.changePassword(userPrincipal.userId(), request);
        return ResponseEntity.noContent().build();
    }

}
