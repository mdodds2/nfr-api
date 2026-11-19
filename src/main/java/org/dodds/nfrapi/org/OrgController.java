package org.dodds.nfrapi.org;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.authentication.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/organizations")
@Tag(name = "Orgs", description = "Orgs controller description")
public class OrgController {

    private final OrgService orgService;

    @GetMapping
    public Iterable<OrgDto> getAllOrgsForUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication,
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy ){

        var isAdmin = isAdmin(authentication);
        return orgService.getAllOrganizations(principal.userId(), isAdmin, sortBy);
    }

    @GetMapping("/{id}")
    public OrgDto getOrgForUser(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return orgService.getOrganization(id, principal.userId());
    }

    @GetMapping("/{orgId}/members")
    public List<OrgMemberDto> getMembersForOrg(
            @AuthenticationPrincipal UserPrincipal principal,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication,
            @PathVariable UUID orgId,
            @RequestParam(required = false, defaultValue = "name", name = "sort") String sortBy) {

        var isAdmin = isAdmin(authentication);
        return orgService.getMembersForOrganization(orgId, principal.userId(), isAdmin, sortBy);
    }

    @PostMapping("/{orgId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public OrgMemberDto addMemberToOrg(
            @AuthenticationPrincipal UserPrincipal principal,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication,
            @PathVariable UUID orgId,
            @Valid @RequestBody AddMemberToOrgRequest request) {
        return orgService.addMemberToOrganizationByEmail(orgId, principal.userId(), isAdmin(authentication), request);
    }

    @PatchMapping("/{orgId}/members/{targetUserId}")
    public OrgMemberDto updateMemberRole(
            @AuthenticationPrincipal UserPrincipal principal,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication,
            @PathVariable UUID orgId,
            @PathVariable UUID targetUserId,
            @Valid @RequestBody UpdateMemberRoleRequest request) {
        return orgService.updateMemberRole(orgId, targetUserId, principal.userId(), isAdmin(authentication), request);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

}
