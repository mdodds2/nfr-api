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
public class OrgMemberDto {
    private UUID userId;
    private String name;
    private String email;
    private OrgRole orgRole;
}
