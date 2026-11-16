package org.dodds.nfrapi.org;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.authentication.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/organizations")
@Tag(name = "Orgs", description = "Orgs controller description")
public class OrgController {

    private final OrgService orgService;

//    @GetMapping
//    public Iterable<ReportDto> getAllOrgs(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy ){
//        return orgService.getAllReports(userPrincipal.userId, sortBy);
//    }

    @GetMapping("/{id}")
    public OrgDto getOrg(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return orgService.getOrganization(id, userPrincipal.userId());
    }

}
