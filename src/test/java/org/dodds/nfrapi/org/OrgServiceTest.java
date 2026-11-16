package org.dodds.nfrapi.org;

import org.dodds.nfrapi.users.Role;
import org.dodds.nfrapi.users.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void shouldRetrieveOrgsForAUserSortedByName() {
        // Arrange
        var userId = UUID.randomUUID();
        var user = User.builder().id(userId).name("Test User").build();
        var org = Organization.builder().id(UUID.randomUUID()).name("Unit Test Organization1").slug("unit-test-organization1").build();
        var dto = OrgDto.builder().id(org.getId()).name(org.getName()).slug(org.getSlug()).build();
        var membership = OrgMembership.builder().id(UUID.randomUUID()).organization(org).orgRole(OrgRole.MEMBER).user(user).build();

        when(orgMapper.toDto(org)).thenReturn(dto);
        when(membershipRepository.findByUserId(userId, Sort.by("organization.name"))).thenReturn(List.of(membership));

        // Act
        var result = orgService.getAllOrganizations(userId, false, "name");

        // Assert
        assertThat(result).containsExactly(dto);
    }

    @Test
    void shouldRetrieveOrgsForAUserSortedBySlug() {
        // Arrange
        var userId = UUID.randomUUID();
        var user = User.builder().id(userId).name("Test User").build();
        var org = Organization.builder().id(UUID.randomUUID()).name("Unit Test Organization1").slug("unit-test-organization1").build();
        var dto = OrgDto.builder().id(org.getId()).name(org.getName()).slug(org.getSlug()).build();
        var membership = OrgMembership.builder().id(UUID.randomUUID()).organization(org).orgRole(OrgRole.MEMBER).user(user).build();

        when(orgMapper.toDto(org)).thenReturn(dto);
        when(membershipRepository.findByUserId(userId, Sort.by("organization.slug"))).thenReturn(List.of(membership));

        // Act
        var result = orgService.getAllOrganizations(userId, false, "slug");

        // Assert
        assertThat(result).containsExactly(dto);
    }

    @Test
    void shouldRetrieveOrgsForPlatformAdmin() {
        // Arrange
        var userId = UUID.randomUUID();
        var org = Organization.builder().id(UUID.randomUUID()).name("Unit Test Organization1").slug("unit-test-organization1").build();
        var dto = OrgDto.builder().id(org.getId()).name(org.getName()).slug(org.getSlug()).build();

        when(orgMapper.toDto(org)).thenReturn(dto);
        when(orgRepository.findAll(Sort.by("name"))).thenReturn(List.of(org));

        // Act
        var result = orgService.getAllOrganizations(userId, true, "name");

        // Assert
        assertThat(result).containsExactly(dto);
        verify(membershipRepository, never()).findByUserId(any(), any());
    }

    @Test
    void shouldNotRetrieveOrgsNotAMember() {
        // Arrange
        var userId = UUID.randomUUID();
        when(membershipRepository.findByUserId(userId, Sort.by("organization.name"))).thenReturn(List.of());

        // Act
        var result = orgService.getAllOrganizations(userId, false, "name");

        // Assert
        assertThat(result).isEmpty();
        verify(orgMapper, never()).toDto(any());
    }


}