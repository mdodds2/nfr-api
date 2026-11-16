package org.dodds.nfrapi.org;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dodds.nfrapi.authentication.JwtService;
import org.dodds.nfrapi.authentication.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrgController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip JWT/security filters for now
public class OrgControllerTest {

    private UUID userId;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OrgService orgService;

    @MockitoBean
    JwtService jwtService;   // JwtAuthenticationFilter is a @Component and needs it

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(getTestAuthToken(userId));
    }
    
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAnOrgAndReadThePrincipal() throws Exception {
        var orgId = UUID.randomUUID();
        var org = OrgDto.builder().id(orgId).name("Org 1").slug("org-1").build();

        when(orgService.getOrganization(orgId, userId)).thenReturn(org);

        mockMvc.perform(get("/organizations/{id}", orgId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(org.getId().toString()))
                .andExpect(jsonPath("$.name").value(org.getName()))
                .andExpect(jsonPath("$.slug").value(org.getSlug()));
    }

    @Test
    void shouldReturn404WhenOrgNotFound() throws Exception {
        var orgId = UUID.randomUUID();
        when(orgService.getOrganization(orgId, userId)).thenThrow(new OrganizationNotFoundException());

        mockMvc.perform(get("/organizations/{id}", orgId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Organization not found."));
    }

    private UsernamePasswordAuthenticationToken getTestAuthToken(UUID userId) {
        return new UsernamePasswordAuthenticationToken(
                new UserPrincipal(userId, null, null),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

}
