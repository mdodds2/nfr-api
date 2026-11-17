package org.dodds.nfrapi.org;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgMembershipDto {
    private UUID id;
    private String name;
    private String slug;
    private OrgRole orgRole;
}
