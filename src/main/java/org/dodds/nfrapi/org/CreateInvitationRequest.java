package org.dodds.nfrapi.org;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateInvitationRequest {
    private UUID orgId;
    private String email;
    private OrgRole orgRole;
}

