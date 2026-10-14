package org.dodds.nfrapi.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dodds.nfrapi.authentication.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
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


}
