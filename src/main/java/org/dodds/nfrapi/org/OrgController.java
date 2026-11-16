package org.dodds.nfrapi.org;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.authentication.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

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

        var isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return orgService.getAllOrganizations(principal.userId(), isAdmin, sortBy);
    }

    @GetMapping("/{id}")
    public OrgDto getOrgForUser(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return orgService.getOrganization(id, userPrincipal.userId());
    }

}
