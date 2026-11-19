package org.dodds.nfrapi.org;

import org.dodds.nfrapi.users.Role;
import org.dodds.nfrapi.users.User;
import org.dodds.nfrapi.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;

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
    UserRepository userRepository;

    @Mock
    OrgMapper orgMapper;

    @Mock
    OrgMembershipMapper membershipMapper;

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

    @Test
    void shouldRetrieveMembersForAnOrgAdmin() {
        // Arrange
        var orgId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var member = OrgMembership.builder()
                        .id(userId)
                        .organization(Organization.builder().id(orgId).build())
                        .user(User.builder().id(userId).role(Role.ADMIN).build())
                        .build();

        var members = List.of(member);

        var memberDto = OrgMemberDto.builder()
                        .userId(userId)
                        .name("Test User")
                        .email("test.user@domain.com")
                        .orgRole(OrgRole.MEMBER)
                        .build();

        when(orgRepository.existsById(orgId)).thenReturn(true);
        when(membershipRepository.findByOrganizationId(orgId, Sort.by("user.name"))).thenReturn(members);
        when(membershipMapper.toMemberDto(member)).thenReturn(memberDto);
        // Act
        var result = orgService.getMembersForOrganization(orgId, userId, true, "name");

        // Assert
        assertThat(result).contains(memberDto);
    }

    @Test
    void shouldRetrieveMembersForAnOrgAdminSortedByEmail() {
        // Arrange
        var orgId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var member = OrgMembership.builder()
                .id(userId)
                .organization(Organization.builder().id(orgId).build())
                .user(User.builder().id(userId).role(Role.ADMIN).build())
                .build();

        var members = List.of(member);

        var memberDto = OrgMemberDto.builder()
                .userId(userId)
                .name("Test User")
                .email("test.user@domain.com")
                .orgRole(OrgRole.MEMBER)
                .build();

        when(orgRepository.existsById(orgId)).thenReturn(true);
        when(membershipRepository.findByOrganizationId(orgId, Sort.by("user.email"))).thenReturn(members);
        when(membershipMapper.toMemberDto(member)).thenReturn(memberDto);

        // Act
        var result = orgService.getMembersForOrganization(orgId, userId, true, "email");

        // Assert
        assertThat(result).contains(memberDto);
    }

    @Test
    void shouldNotRetrieveMembersForAnOrgAdmin() {
        // Arrange
        var orgId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        when(orgRepository.existsById(orgId)).thenReturn(false);

        // Act

        // Assert
        assertThrows(OrganizationNotFoundException.class, () -> orgService.getMembersForOrganization(orgId, userId, true, "name"));
    }

    @Test
    void shouldRetrieveMembersForAnOrgOrgAdmin() {
        // Arrange
        var orgId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var user = User.builder().id(userId).role(Role.USER).build();
        var org = Organization.builder().id(orgId).build();

        var member = OrgMembership.builder()
                .id(userId)
                .organization(org)
                .user(user)
                .build();

        var members = List.of(member);

        var memberDto = OrgMemberDto.builder()
                .userId(userId)
                .name("Test User")
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        var membership = OrgMembership.builder()
                .id(UUID.randomUUID())
                .user(user)
                .orgRole(OrgRole.ORG_ADMIN)
                .organization(org)
                .build();

        //when(orgRepository.existsById(orgId)).thenReturn(true);
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, userId)).thenReturn(Optional.of(membership));
        when(membershipMapper.toMemberDto(member)).thenReturn(memberDto);
        when(membershipRepository.findByOrganizationId(orgId, Sort.by("user.email"))).thenReturn(members);
        when(membershipMapper.toMemberDto(member)).thenReturn(memberDto);

        // Act
        var result = orgService.getMembersForOrganization(orgId, userId, false, "email");

        // Assert
        assertThat(result).contains(memberDto);
    }

    @Test
    void shouldNotRetrieveMembersForAnOrgOrgAdmin() {
        // Arrange
        var orgId = UUID.randomUUID();
        var userId = UUID.randomUUID();

        var user = User.builder().id(userId).role(Role.USER).build();
        var org = Organization.builder().id(orgId).build();

        var membership = OrgMembership.builder()
                .id(UUID.randomUUID())
                .user(user)
                .orgRole(OrgRole.MEMBER)
                .organization(org)
                .build();

        //when(orgRepository.existsById(orgId)).thenReturn(true);
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, userId)).thenReturn(Optional.of(membership));

        // Act

        // Assert
        assertThrows(AccessDeniedException.class, () -> orgService.getMembersForOrganization(orgId, userId, false, "email"));
    }

    @Test
    void shouldAddMemberToOrganizationByEmail() {
        // Arrange
        var orgId = UUID.randomUUID();
        var callerId = UUID.randomUUID();
        var newUserId = UUID.randomUUID();

        var request = AddMemberToOrgRequest.builder()
                .email("test.user@domain.com")
                .orgRole(OrgRole.MEMBER)
                .build();

        var user = User.builder()
                .id(newUserId)
                .name("Test User")
                .email("test.user@domain.com")
                .role(Role.ADMIN)
                .build();

        var org = Organization.builder()
                .id(orgId)
                .name("TEST ORGANIZATION")
                .slug("test-organization")
                .build();

        var orgMembershipId = UUID.randomUUID();
        var orgMembership = OrgMembership.builder()
                .organization(org)
                .orgRole(OrgRole.MEMBER)
                .id(orgMembershipId)
                .build();

        var orgMemberDto = OrgMemberDto.builder()
                .userId(newUserId)
                .name("TEST USER")
                .email("test.user@domain.com")
                .orgRole(OrgRole.MEMBER)
                .build();

        when(orgRepository.existsById(orgId)).thenReturn(true);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(orgRepository.getReferenceById(orgId)).thenReturn(org);
        when(membershipRepository.existsByOrganizationIdAndUserId(orgId, newUserId)).thenReturn(false);
        when(membershipRepository.save(any(OrgMembership.class))).thenReturn(orgMembership);
        when(membershipMapper.toMemberDto(orgMembership)).thenReturn(orgMemberDto);

        // Act
        var result = orgService.addMemberToOrganizationByEmail(orgId, callerId, true, request);

        // Assert
        assertThat(result).isEqualTo(orgMemberDto);
    }

    @Test
    void shouldAddMemberToOrganizationByEmailAsOrgAdmin() {
        // Arrange
        var orgId = UUID.randomUUID();
        var callerId = UUID.randomUUID();
        var newUserId = UUID.randomUUID();

        var request = AddMemberToOrgRequest.builder()
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        var user = User.builder()
                .id(newUserId)
                .name("Test User")
                .email("test.user@domain.com")
                .role(Role.USER)
                .build();

        var org = Organization.builder()
                .id(orgId)
                .name("TEST ORGANIZATION")
                .slug("test-organization")
                .build();

        var orgMembershipId = UUID.randomUUID();
        var orgMembership = OrgMembership.builder()
                .user(user)
                .organization(org)
                .orgRole(OrgRole.ORG_ADMIN)
                .id(orgMembershipId)
                .build();

        var orgMemberDto = OrgMemberDto.builder()
                .userId(newUserId)
                .name("TEST USER")
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        var callerMembership = OrgMembership.builder().organization(org).orgRole(OrgRole.ORG_ADMIN).build();
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, callerId)).thenReturn(Optional.of(callerMembership));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(orgRepository.getReferenceById(orgId)).thenReturn(org);
        when(membershipRepository.existsByOrganizationIdAndUserId(orgId, newUserId)).thenReturn(false);
        when(membershipRepository.save(any(OrgMembership.class))).thenReturn(orgMembership);
        when(membershipMapper.toMemberDto(orgMembership)).thenReturn(orgMemberDto);

        // Act
        var result = orgService.addMemberToOrganizationByEmail(orgId, callerId, false, request);

        // Assert
        assertThat(result).isEqualTo(orgMemberDto);
    }

    @Test
    void shouldNotAddMemberToOrganizationByEmailOrgNotFound() {
        // Arrange
        var orgId = UUID.randomUUID();
        var callerId = UUID.randomUUID();

        var request = AddMemberToOrgRequest.builder()
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();


        when(orgRepository.existsById(orgId)).thenReturn(false);

        // Act

        // Assert
        assertThrows(OrganizationNotFoundException.class, () -> orgService.addMemberToOrganizationByEmail(orgId, callerId, true, request));
    }

    @Test
    void shouldNotAddMemberToOrganizationByEmailUserExistsInOrg() {
        // Arrange
        var orgId = UUID.randomUUID();
        var callerId = UUID.randomUUID();
        var newUserId = UUID.randomUUID();

        var user = User.builder()
                .id(newUserId)
                .name("Test User")
                .email("test.user@domain.com")
                .role(Role.USER)
                .build();

        var request = AddMemberToOrgRequest.builder()
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        var org = Organization.builder()
                .id(orgId)
                .name("TEST ORGANIZATION")
                .slug("test-organization")
                .build();

        when(orgRepository.existsById(orgId)).thenReturn(true);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(orgRepository.getReferenceById(orgId)).thenReturn(org);
        when(membershipRepository.existsByOrganizationIdAndUserId(orgId, user.getId())).thenReturn(true);

        // Act

        // Assert
        assertThrows(UserExistsInOrganizationException.class, () -> orgService.addMemberToOrganizationByEmail(orgId, callerId, true, request));
    }

    @Test
    void shouldUpdateAMemberRole() {
        // Arrange
        var orgId = UUID.randomUUID();
        var callerId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        var request = UpdateMemberRoleRequest.builder()
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        var user = User.builder()
                .id(targetUserId)
                .name("Test User")
                .email("test.user@domain.com")
                .role(Role.USER)
                .build();

        var org = Organization.builder()
                .id(orgId)
                .name("TEST ORGANIZATION")
                .slug("test-organization")
                .build();

        var orgMembershipId = UUID.randomUUID();
        var orgMembership = OrgMembership.builder()
                .user(user)
                .organization(org)
                .orgRole(OrgRole.ORG_ADMIN)
                .id(orgMembershipId)
                .build();

        var orgMemberDto = OrgMemberDto.builder()
                .userId(targetUserId)
                .name("TEST USER")
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        var callerMembership = OrgMembership.builder().organization(org).orgRole(OrgRole.MEMBER).build();
        when(orgRepository.existsById(orgId)).thenReturn(true);
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, targetUserId)).thenReturn(Optional.of(callerMembership));
        when(membershipRepository.save(any(OrgMembership.class))).thenReturn(orgMembership);
        when(membershipMapper.toMemberDto(orgMembership)).thenReturn(orgMemberDto);

        // Act
        var result = orgService.updateMemberRole(orgId, targetUserId, callerId, true, request);

        // Assert
        assertThat(result).isEqualTo(orgMemberDto);
    }

    @Test
    void shouldUpdateAMemberRoleOrgAdmin() {
        // Arrange
        var orgId = UUID.randomUUID();
        var callerId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        var request = UpdateMemberRoleRequest.builder()
                .orgRole(OrgRole.MEMBER)
                .build();

        var user = User.builder()
                .id(targetUserId)
                .name("Test User")
                .email("test.user@domain.com")
                .role(Role.USER)
                .build();

        var org = Organization.builder()
                .id(orgId)
                .name("TEST ORGANIZATION")
                .slug("test-organization")
                .build();

        var orgMembershipId = UUID.randomUUID();
        var orgMembership = OrgMembership.builder()
                .user(user)
                .organization(org)
                .orgRole(OrgRole.ORG_ADMIN)
                .id(orgMembershipId)
                .build();

        var orgMemberDto = OrgMemberDto.builder()
                .userId(targetUserId)
                .name("TEST USER")
                .email("test.user@domain.com")
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        var callerMembership = OrgMembership.builder().organization(org).orgRole(OrgRole.ORG_ADMIN).build();
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, callerId)).thenReturn(Optional.of(callerMembership));
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, targetUserId)).thenReturn(Optional.of(orgMembership));
        when(membershipRepository.save(any(OrgMembership.class))).thenReturn(orgMembership);
        when(membershipMapper.toMemberDto(orgMembership)).thenReturn(orgMemberDto);
        when(membershipRepository.countByOrganizationIdAndOrgRole(orgId, OrgRole.ORG_ADMIN)).thenReturn(2L);

        // Act
        var result = orgService.updateMemberRole(orgId, targetUserId, callerId, false, request);

        // Assert
        assertThat(result).isEqualTo(orgMemberDto);

    }

    @Test
    void shouldNotUpdateAMemberRoleLastOrgAdmin() {
        // Arrange
        var orgId = UUID.randomUUID();
        var callerId = UUID.randomUUID();
        var targetUserId = UUID.randomUUID();

        var request = UpdateMemberRoleRequest.builder()
                .orgRole(OrgRole.MEMBER)
                .build();

        var user = User.builder()
                .id(targetUserId)
                .name("Test User")
                .email("test.user@domain.com")
                .role(Role.USER)
                .build();

        var org = Organization.builder()
                .id(orgId)
                .name("TEST ORGANIZATION")
                .slug("test-organization")
                .build();

        var orgMembershipId = UUID.randomUUID();
        var orgMembership = OrgMembership.builder()
                .user(user)
                .organization(org)
                .orgRole(OrgRole.ORG_ADMIN)
                .id(orgMembershipId)
                .build();

        var callerMembership = OrgMembership.builder().organization(org).orgRole(OrgRole.ORG_ADMIN).build();
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, callerId)).thenReturn(Optional.of(callerMembership));
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, targetUserId)).thenReturn(Optional.of(orgMembership));
        when(membershipRepository.countByOrganizationIdAndOrgRole(orgId, OrgRole.ORG_ADMIN)).thenReturn(1L);

        // Act

        // Assert
        assertThrows(LastOrgAdminException.class, () -> orgService.updateMemberRole(orgId, targetUserId, callerId, false, request));
        verify(membershipRepository, never()).save(any());
    }

}