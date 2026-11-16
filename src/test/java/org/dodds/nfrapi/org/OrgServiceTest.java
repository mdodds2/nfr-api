package org.dodds.nfrapi.org;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgServiceTest {

    @Mock
    OrgRepository orgRepository;

    @Mock
    OrgMembershipRepository membershipRepository;

    @Mock
    OrgMapper orgMapper;

    @InjectMocks
    OrgService orgService;

    @Test
    void shouldRetrieveAnOrg() {
        // Arrange
        var orgId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var org = Organization.builder()
                .id(orgId)
                .name("Unit Test Organization")
                .slug("unit-test-organization")
                .build();
        var dto = OrgDto.builder()
                .id(orgId)
                .name(org.getName())
                .slug(org.getSlug())
                .build();

        when(orgMapper.toDto(org)).thenReturn(dto);
        when(membershipRepository.existsByOrganizationIdAndUserId(orgId, userId)).thenReturn(true);
        when(orgRepository.findById(orgId)).thenReturn(Optional.of(org));

        // Act
        OrgDto result = orgService.getOrganization(orgId, userId);

        // Assert
        assertEquals(dto, result);
        verify(orgRepository).findById(orgId);
    }

    @Test
    void shouldNotRetrieveAnOrgNotAMember() {
        // Arrange
        var orgId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        when(membershipRepository.existsByOrganizationIdAndUserId(orgId, userId)).thenReturn(false);

        // Act

        // Assert
        assertThrows(OrganizationNotFoundException.class, () -> orgService.getOrganization(orgId, userId));
        verify(orgRepository, never()).findById(orgId);
    }

    @Test
    void shouldNotRetrieveAnOrgMissingOrg() {
        // Arrange
        var orgId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        when(membershipRepository.existsByOrganizationIdAndUserId(orgId, userId)).thenReturn(true);
        when(orgRepository.findById(orgId)).thenReturn(Optional.empty());

        // Act

        // Assert
        assertThrows(OrganizationNotFoundException.class, () -> orgService.getOrganization(orgId, userId));
        verify(membershipRepository).existsByOrganizationIdAndUserId(orgId, userId);
    }

}