package org.dodds.nfrapi.org;

import org.dodds.nfrapi.common.TokenHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvitationServiceTest {

    @Captor
    ArgumentCaptor<Invitation> invitationCaptor;

    @Mock
    InvitationRepository invitationRepository;

    @Mock
    InvitationMapper invitationMapper;

    @Mock
    TokenHasher tokenHasher;

    @Mock
    OrgRepository orgRepository;

    @InjectMocks
    InvitationService invitationService;

    @Test
    void shouldCreateAnInvitation() {
        var orgId = UUID.randomUUID();
        var sender = "sender@domain.com";

        var request = CreateInvitationRequest.builder()
                .orgId(orgId)
                .email("Test.User@Domain.com")
                .orgRole(OrgRole.MEMBER)
                .build();

        var mapped = Invitation.builder()
                .orgId(orgId)
                .email(request.getEmail())
                .orgRole(OrgRole.MEMBER)
                .build();

        when(orgRepository.existsById(orgId)).thenReturn(true);
        when(invitationMapper.toEntity(request)).thenReturn(mapped);
        when(tokenHasher.generateToken()).thenReturn("TOKEN");
        when(tokenHasher.hash("TOKEN")).thenReturn("HASHED");

        String token = invitationService.createInvitation(request, sender);

        verify(invitationRepository).save(invitationCaptor.capture());

        var saved = invitationCaptor.getValue();
        assertThat(token).isNotBlank();
        assertThat(token).isEqualTo("TOKEN");
        assertThat(saved.getTokenHash()).isEqualTo("HASHED");
        assertThat(saved.getEmail()).isEqualTo("test.user@domain.com");
        assertThat(saved.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(saved.getSenderEmail()).isEqualTo(sender);
        assertThat(saved.getExpiration())
                .isBetween(LocalDateTime.now().plusDays(7).minusMinutes(1), LocalDateTime.now().plusDays(7));
    }

    @Test
    void shouldNotCreateAnInvitationOrgNotFound() {
        var orgId = UUID.randomUUID();
        var sender = "sender@domain.com";

        var request = CreateInvitationRequest.builder()
                .orgId(orgId)
                .email("Test.User@Domain.com")
                .orgRole(OrgRole.MEMBER)
                .build();

        when(orgRepository.existsById(orgId)).thenReturn(false);

        assertThrows(OrganizationNotFoundException.class, () -> invitationService.createInvitation(request, sender));
        verify(invitationRepository, never()).save(any());
    }

}
