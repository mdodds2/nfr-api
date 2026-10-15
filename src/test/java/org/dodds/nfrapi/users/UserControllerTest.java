package org.dodds.nfrapi.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dodds.nfrapi.authentication.JwtService;
import org.dodds.nfrapi.report.ReportDto;
import org.dodds.nfrapi.report.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip JWT/security filters for now
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReportService reportService;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtService jwtService;   // JwtAuthenticationFilter is a @Component and needs it

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
        var userDto = UserDto.builder().id(id).name("Test User1").email("user1@example.com").build();

        when(userService.getUser(id)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void shouldNotGetAUserNotFound() throws Exception {
        var id = UUID.randomUUID();
        var userDto = UserDto.builder().id(id).name("Test User1").email("user1@example.com").build();

        when(userService.getUser(id)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void shouldGetReportsForAUser() throws Exception {
        var id = UUID.randomUUID();
        var report1 = ReportDto.builder().id(UUID.randomUUID()).name("Report1").build();
        var report2 = ReportDto.builder().id(UUID.randomUUID()).name("Report2").build();

        when(reportService.getReportsForUser(id, "name")).thenReturn(List.of(report1, report2));

        mockMvc.perform(get("/users/{id}/reports", id).param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(report1.getId().toString()));

        verify(reportService).getReportsForUser(id, "name");
    }

    @Test
    void shouldGetReportsForUserWithDefaultSort() throws Exception {
        var id = UUID.randomUUID();
        var report = ReportDto.builder().id(UUID.randomUUID()).build();

        when(reportService.getReportsForUser(id, "createdAt")).thenReturn(List.of(report));

        mockMvc.perform(get("/users/{id}/reports", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(report.getId().toString()));

        verify(reportService).getReportsForUser(id, "createdAt");
    }

    @Test
    void shouldCreateAUser() throws Exception {
        var userId = UUID.randomUUID();
        var request = RegisterUserRequest.builder().name("Test User").email("user@example.com").password("ENCODED").build();
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
        var userId = UUID.randomUUID();
        var request = RegisterUserRequest.builder().name("Test User").email("user@example.com").password("ENCODED").build();
        var userDto = UserDto.builder().id(userId).name("Test User").email("user@example.com").build();

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
        var request = UpdateUserRequest.builder().name("Test User").email("user@example.com").password("NEW").build();
        var userDto = UserDto.builder().id(id).name("Test User").email("user@example.com").build();

        when(userService.updateUser(id, request)).thenReturn(userDto);

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(userService).updateUser(id, request);
    }

    @Test
    void shouldNotUpdateAUserNotFound() throws Exception {
        var id = UUID.randomUUID();
        var request = UpdateUserRequest.builder().name("Test User").email("user@example.com").password("NEW").build();

        when(userService.updateUser(id, request)).thenThrow(new UserNotFoundException());

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
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
    void shouldChangePassword() throws Exception {
        var id = UUID.randomUUID();
        var request = ChangePasswordRequest.builder().oldPassword("OLD").newPassword("NEW").build();

        doNothing().when(userService).changePassword(id, request);

        mockMvc.perform(post("/users/{id}/change-password", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).changePassword(eq(id), any());
    }

    @Test
    void shouldNotChangePasswordBadPassword() throws Exception {
        var id = UUID.randomUUID();
        var request = ChangePasswordRequest.builder().oldPassword("BAD").newPassword("NEW").build();

        doThrow(AccessDeniedException.class).when(userService).changePassword(id, request);

        mockMvc.perform(post("/users/{id}/change-password", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(userService).changePassword(eq(id), any());
    }

}
