package org.dodds.nfrapi.org;

import java.time.LocalDateTime;
import java.util.UUID;

public class InvitationDto {
    private UUID id;
    private UUID orgId;
    private String email;
    private OrgRole orgRole;
    private String tokenHash;
    private LocalDateTime expiration;
    private InvitationStatus status;
    private String senderEmail;
}

