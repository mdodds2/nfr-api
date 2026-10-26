package org.dodds.nfrapi.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dodds.nfrapi.authentication.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip JWT/security filters for now
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtService jwtService;   // JwtAuthenticationFilter is a @Component and needs it

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(UUID id) {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(id, null, List.of()));
    }

    @Test
    void shouldGetAllUsersSortedByEmail() throws Exception {

        var user1 = UserDto.builder().id(UUID.randomUUID()).name("Test User1").email("user1@example.com").build();
        var user2 = UserDto.builder().id(UUID.randomUUID()).name("Test User2").email("user2@example.com").build();
        var user3 = UserDto.builder().id(UUID.randomUUID()).name("Test User3").email("user3@example.com").build();

        when(userService.getAllUsers("email")).thenReturn(List.of(user1, user2, user3));

        mockMvc.perform(get("/users").param("sort", "email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(user1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(user2.getId().toString()))
                .andExpect(jsonPath("$[2].id").value(user3.getId().toString()));
    }

    @Test
    void shouldGetAUser() throws Exception {
        var id = UUID.randomUUID();
        var userDto = UserDto.builder()
                .id(id)
                .name("Test User1")
                .email("user1@example.com")
                .build();

        when(userService.getUser(any())).thenReturn(userDto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void shouldNotGetAUserNotFound() throws Exception {
        var id = UUID.randomUUID();

        when(userService.getUser(id)).thenThrow(new UserNotFoundException());

        loginAs(id);
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void shouldCreateAUser() throws Exception {
        var userId = UUID.randomUUID();
        var request = RegisterUserRequest.builder().name("Test User").email("user@example.com").password("ENCODED1").build();
        var userDto = UserDto.builder().id(userId).name("Test User").email("user@example.com").build();

        when(userService.registerUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/users/" + userId)))
                .andExpect(jsonPath("$.id").value(userId.toString()));

        verify(userService).registerUser(request);
    }

    @Test
    void shouldNotCreateAUserDuplicateEmail() throws Exception {
        var request = RegisterUserRequest.builder().name("Test User").email("user@example.com").password("ENCODED1").build();

        when(userService.registerUser(any())).thenThrow(new DuplicateUserException());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already exists."));
    }

    @Test
    void shouldNotCreateAUserMissingCredentials() throws Exception {
        var request = RegisterUserRequest.builder().name("Test User").build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateAUser() throws Exception {
        var id = UUID.randomUUID();
        var request = UpdateUserRequest.builder().name("Test User").email("user@example.com").build();
        var userDto = UserDto.builder().id(id).name("Test User").email("user@example.com").build();

        when(userService.updateUser(eq(id), any(UpdateUserRequest.class))).thenReturn(userDto);

        loginAs(id);
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(userService).updateUser(id, request);
    }

    @Test
    void shouldNotUpdateAUserNotFound() throws Exception {
        var id = UUID.randomUUID();
        var request = UpdateUserRequest.builder().name("Test User").email("user@example.com").build();

        when(userService.updateUser(eq(id), any(UpdateUserRequest.class))).thenThrow(new UserNotFoundException());

        loginAs(id);
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateAUserBadRequest() throws Exception {
        var id = UUID.randomUUID();
        var request = UpdateUserRequest.builder().name("Test User").email("not-an-email").build();

        loginAs(id);
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotUpdateAUserNoToken() throws Exception {
        var id = UUID.randomUUID();
        var request = UpdateUserRequest.builder().name("Test User").email("not-an-email").build();

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteAUser() throws Exception {
        var id = UUID.randomUUID();

        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(id);
    }

    @Test
    void shouldNotDeleteAUserNotFound() throws Exception {
        var id = UUID.randomUUID();

        doThrow(new UserNotFoundException()).when(userService).deleteUser(id);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldChangeLoggedInUserPassword() throws Exception {
        var id = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(
                id,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        var request = ChangePasswordRequest.builder().oldPassword("OLDPASSWORD").newPassword("NEWPASSWORD").build();

        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            mockMvc.perform(post("/users/me/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(userService).changePassword(eq(id), any());
    }

    @Test
    void shouldNotChangeLoggedInUserNotFound() throws Exception {
        var id = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(
                id,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        var request = ChangePasswordRequest.builder().oldPassword("OLDPASSWORD").newPassword("NEWPASSWORD").build();

        doThrow(UserNotFoundException.class).when(userService).changePassword(eq(id), any());

        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            mockMvc.perform(post("/users/me/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void shouldNotChangeLoggedInUserForbidden() throws Exception {
        var id = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(
                id,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        var request = ChangePasswordRequest.builder().oldPassword("OLDPASSWORD").newPassword("NEWPASSWORD").build();

        doThrow(AccessDeniedException.class).when(userService).changePassword(eq(id), any());

        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            mockMvc.perform(post("/users/me/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(userService).changePassword(eq(id), any());
    }

    @Test
    void shouldNotChangeLoggedInUserBadRequest() throws Exception {
        var request = ChangePasswordRequest.builder().oldPassword("OLDPASSWORD").newPassword("NEW").build();

        mockMvc.perform(post("/users/me/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}