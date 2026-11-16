package org.dodds.nfrapi.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

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
    void shouldReturn200ForReportsWhenNotAdmin() throws Exception {
        mockMvc.perform(get("/reports").with(authentication(getTestAuthToken("USER"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForUsersWhenAdmin() throws Exception {
        mockMvc.perform(get("/users").with(authentication(getTestAuthToken("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403ForUsersWhenNotAdmin() throws Exception {
        mockMvc.perform(get("/users").with(authentication(getTestAuthToken("USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn200ForReportsWhenAdmin() throws Exception {
        mockMvc.perform(get("/reports").with(authentication(getTestAuthToken("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotReturn401ForAnonymousLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private UsernamePasswordAuthenticationToken getTestAuthToken(String role) {
        return new UsernamePasswordAuthenticationToken(
                new UserPrincipal(UUID.randomUUID(), null, null),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

}