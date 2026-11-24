package org.dodds.nfrapi.org;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name="org_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrgRole orgRole;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name="expiration", nullable = false)
    private LocalDateTime expiration;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    @Column(name = "sender_email", nullable = false)
    private String senderEmail;
}

