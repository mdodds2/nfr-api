package org.dodds.nfrapi.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired MockMvc mockMvc;

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn200ForReportsWhenNotAdmin() throws Exception {
        mockMvc.perform(get("/reports")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn200ForUsersWhenAdmin() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403ForUsersWhenNotAdmin() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn200ForReportsWhenAdmin() throws Exception {
        mockMvc.perform(get("/reports")).andExpect(status().isOk());
    }

    @Test
    void shouldNotReturn401ForAnonymousLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isBadRequest());
    }

}