package org.dodds.nfrapi.org;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dodds.nfrapi.authentication.JwtService;
import org.dodds.nfrapi.authentication.UserPrincipal;
import org.dodds.nfrapi.users.Role;
import org.dodds.nfrapi.users.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
        loginAs(Role.USER);
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

    @Test
    void shouldGetAllOrgsForUser() throws Exception {
        var orgId = UUID.randomUUID();
        var orgDto = OrgDto.builder().id(orgId).name("Org 1").slug("org-1").build();

        when(orgService.getAllOrganizations(userId, false,"slug")).thenReturn(List.of(orgDto));

        mockMvc.perform(get("/organizations").param("sort", "slug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orgDto.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(orgDto.getName()))
                .andExpect(jsonPath("$[0].slug").value(orgDto.getSlug()))
        ;
    }

    @Test
    void shouldGetAllOrgsForUserJustSortedDifferently() throws Exception {
        var orgId = UUID.randomUUID();
        var orgDto = OrgDto.builder().id(orgId).name("Org 1").slug("org-1").build();

        when(orgService.getAllOrganizations(userId, false,"name")).thenReturn(List.of(orgDto));

        mockMvc.perform(get("/organizations").param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orgDto.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(orgDto.getName()))
                .andExpect(jsonPath("$[0].slug").value(orgDto.getSlug()));
    }

    @Test
    void shouldGetAllOrgsForPlatformAdmin() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var orgDto = OrgDto.builder().id(orgId).name("Org 1").slug("org-1").build();

        when(orgService.getAllOrganizations(userId, true,"name")).thenReturn(List.of(orgDto));

        mockMvc.perform(get("/organizations").param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orgDto.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(orgDto.getName()))
                .andExpect(jsonPath("$[0].slug").value(orgDto.getSlug()));
    }

    @Test
    void shouldGetAllOrgMembers() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var memberDto = OrgMemberDto.builder()
                .userId(userId)
                .orgRole(OrgRole.ORG_ADMIN)
                .name("Test User")
                .email("test.user@domain.com")
                .build();

        when(orgService.getMembersForOrganization(orgId, userId, true, "name")).thenReturn(List.of(memberDto));

        mockMvc.perform(get("/organizations/{id}/members", orgId).param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(memberDto.getUserId().toString()))
                .andExpect(jsonPath("$[0].name").value(memberDto.getName()))
                .andExpect(jsonPath("$[0].email").value(memberDto.getEmail()));
    }

    @Test
    void shouldNotGetAllOrgMembersOrgForbidden() throws Exception {
        var orgId = UUID.randomUUID();

        when(orgService.getMembersForOrganization(orgId, userId, false, "name")).thenThrow(new AccessDeniedException("Current user is not an ORG_ADMIN"));

        mockMvc.perform(get("/organizations/{id}/members", orgId).param("sort", "name"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotGetAllOrgMembersOrgNotFound() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();

        when(orgService.getMembersForOrganization(orgId, userId, true, "name")).thenThrow(new OrganizationNotFoundException());

        mockMvc.perform(get("/organizations/{id}/members", orgId).param("sort", "name"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddAnExistingUserToAnOrgAsPlatformAdmin() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();

        var memberDto = OrgMemberDto.builder()
                .userId(userId)
                .orgRole(OrgRole.MEMBER)
                .name("Test User")
                .email("test.user@domain.com")
                .build();

        var request = AddMemberToOrgRequest.builder()
                        .email("test.user@domain.com")
                        .orgRole(OrgRole.MEMBER)
                        .build();

        when(orgService.addMemberToOrganizationByEmail(orgId, userId, true, request)).thenReturn(memberDto);

        mockMvc.perform(post("/organizations/{id}/members", orgId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(memberDto.getUserId().toString()))
                .andExpect(jsonPath("$.name").value(memberDto.getName()))
                .andExpect(jsonPath("$.email").value(memberDto.getEmail()));
    }

    @Test
    void shouldAddAnExistingUserToAnOrgAsOrgAdmin() throws Exception {
        var orgId = UUID.randomUUID();

        var memberDto = OrgMemberDto.builder()
                .userId(userId)
                .orgRole(OrgRole.ORG_ADMIN)
                .name("Test User")
                .email("test.user@domain.com")
                .build();

        var request = AddMemberToOrgRequest.builder()
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        when(orgService.addMemberToOrganizationByEmail(orgId, userId, false, request)).thenReturn(memberDto);

        mockMvc.perform(post("/organizations/{id}/members", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(memberDto.getUserId().toString()))
                .andExpect(jsonPath("$.name").value(memberDto.getName()))
                .andExpect(jsonPath("$.email").value(memberDto.getEmail()));
    }

    @Test
    void shouldNotAddAnExistingUserToAnOrgUserNotFound() throws Exception {
        var orgId = UUID.randomUUID();

        var request = AddMemberToOrgRequest.builder()
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        when(orgService.addMemberToOrganizationByEmail(orgId, userId, false, request)).thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/organizations/{id}/members", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotAddAnExistingUserToAnOrgExistingUser() throws Exception {
        var orgId = UUID.randomUUID();

        var request = AddMemberToOrgRequest.builder()
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        when(orgService.addMemberToOrganizationByEmail(orgId, userId, false, request)).thenThrow(new UserExistsInOrganizationException());

        mockMvc.perform(post("/organizations/{id}/members", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotAddAnExistingUserToAnOrgAccessDenied() throws Exception {
        var orgId = UUID.randomUUID();

        var request = AddMemberToOrgRequest.builder()
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        when(orgService.addMemberToOrganizationByEmail(orgId, userId, false, request)).thenThrow(new AccessDeniedException("Current user is not an ORG_ADMIN"));

        mockMvc.perform(post("/organizations/{id}/members", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotAddAnExistingUserToAnOrgInvalidRequest() throws Exception {
        var orgId = UUID.randomUUID();

        var request = AddMemberToOrgRequest.builder()
                .email(null)
                .orgRole(null)
                .build();

        mockMvc.perform(post("/organizations/{id}/members", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(orgService);
    }

    @Test
    void shouldUpdateAMemberRole() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        var request = UpdateMemberRoleRequest.builder().orgRole(OrgRole.ORG_ADMIN).build();
        var memberDto = OrgMemberDto.builder()
                .userId(targetUserId)
                .orgRole(OrgRole.ORG_ADMIN)
                .name("Test User")
                .email("test.user@domain.com")
                .build();

        when(orgService.updateMemberRole(eq(orgId), eq(targetUserId), eq(userId), eq(true), any(UpdateMemberRoleRequest.class))).thenReturn(memberDto);
        mockMvc.perform(patch("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(memberDto.getUserId().toString()))
                .andExpect(jsonPath("$.name").value(memberDto.getName()))
                .andExpect(jsonPath("$.email").value(memberDto.getEmail()))
                .andExpect(jsonPath("$.orgRole").value(memberDto.getOrgRole().toString()));
    }

    @Test
    void shouldUpdateAMemberRoleOrgAdmin() throws Exception {
        loginAs(Role.USER);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        var request = UpdateMemberRoleRequest.builder().orgRole(OrgRole.ORG_ADMIN).build();
        var memberDto = OrgMemberDto.builder()
                .userId(targetUserId)
                .orgRole(OrgRole.ORG_ADMIN)
                .name("Test User")
                .email("test.user@domain.com")
                .build();

        when(orgService.updateMemberRole(eq(orgId), eq(targetUserId), eq(userId), eq(false), any(UpdateMemberRoleRequest.class))).thenReturn(memberDto);
        mockMvc.perform(patch("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(memberDto.getUserId().toString()))
                .andExpect(jsonPath("$.name").value(memberDto.getName()))
                .andExpect(jsonPath("$.email").value(memberDto.getEmail()))
                .andExpect(jsonPath("$.orgRole").value(memberDto.getOrgRole().toString()));
    }

    @Test
    void shouldNotUpdateAMemberRoleOrgNotFound() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        var request = UpdateMemberRoleRequest.builder().orgRole(OrgRole.ORG_ADMIN).build();

        when(orgService.updateMemberRole(eq(orgId), eq(targetUserId), eq(userId), eq(true), any(UpdateMemberRoleRequest.class))).thenThrow(new OrganizationNotFoundException());
        mockMvc.perform(patch("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateAMemberRoleMemberNotFound() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        var request = UpdateMemberRoleRequest.builder().orgRole(OrgRole.ORG_ADMIN).build();

        when(orgService.updateMemberRole(eq(orgId), eq(targetUserId), eq(userId), eq(true), any(UpdateMemberRoleRequest.class))).thenThrow(new MemberNotFoundException());
        mockMvc.perform(patch("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateAMemberRoleLastOrgAdmin() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        var request = UpdateMemberRoleRequest.builder().orgRole(OrgRole.ORG_ADMIN).build();

        when(orgService.updateMemberRole(eq(orgId), eq(targetUserId), eq(userId), eq(true), any(UpdateMemberRoleRequest.class))).thenThrow(new LastOrgAdminException());

        mockMvc.perform(patch("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }


    @Test
    void shouldRemoveMemberFromOrg() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        mockMvc.perform(delete("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId))
                        .andExpect(status().isNoContent());

        verify(orgService).removeMember(orgId, targetUserId, userId, true);
    }

    @Test
    void shouldNotRemoveMemberFromOrgLastAdmin() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        doThrow(LastOrgAdminException.class)
                .when(orgService)
                .removeMember(orgId, targetUserId, userId, true);

        mockMvc.perform(delete("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotRemoveMemberNotFound() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        doThrow(MemberNotFoundException.class)
                .when(orgService)
                .removeMember(orgId, targetUserId, userId, true);

        mockMvc.perform(delete("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotRemoveMemberOrgNotFound() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        doThrow(OrganizationNotFoundException.class)
                .when(orgService)
                .removeMember(orgId, targetUserId, userId, true);

        mockMvc.perform(delete("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotRemoveMemberAccessDenied() throws Exception {
        loginAs(Role.ADMIN);
        var orgId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        doThrow(AccessDeniedException.class)
                .when(orgService)
                .removeMember(orgId, targetUserId, userId, true);

        mockMvc.perform(delete("/organizations/{orgId}/members/{targetUserId}", orgId, targetUserId))
                .andExpect(status().isForbidden());
    }

    private void loginAs(Role role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(userId),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.toString()))
                )
        );
    }

}
