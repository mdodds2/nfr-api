package org.dodds.nfrapi.org;

import lombok.RequiredArgsConstructor;
import org.dodds.nfrapi.users.UserNotFoundException;
import org.dodds.nfrapi.users.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrgService {

    private final OrgRepository orgRepository;
    private final OrgMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final OrgMapper orgMapper;
    private final OrgMembershipMapper membershipMapper;

    public OrgDto getOrganization(UUID orgId, UUID userId) {
        if (!membershipRepository.existsByOrganizationIdAndUserId(orgId, userId)) {
            throw new OrganizationNotFoundException();
        }
        return orgRepository.findById(orgId)
                .map(orgMapper::toDto)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    public List<OrgDto> getAllOrganizations(UUID userId, boolean isPlatformAdmin, String sortBy) {
        var field = "slug".equals(sortBy) ? "slug" : "name";
        var property = isPlatformAdmin ? field : "organization." + field;

        return isPlatformAdmin
                ? orgRepository.findAll(Sort.by(property)).stream().map(orgMapper::toDto).toList()
                : membershipRepository.findByUserId(userId, Sort.by(property)).stream()
                  .map(OrgMembership::getOrganization)
                  .map(orgMapper::toDto)
                  .toList();
    }

    public List<OrgMemberDto> getMembersForOrganization(UUID orgId, UUID userId, boolean isAdmin, String sortBy) {
        if (!isAdmin) {
            requireOrgAdmin(orgId, userId);
        } else if (!orgRepository.existsById(orgId)) {
            throw new OrganizationNotFoundException();
        }

        var field = "email".equals(sortBy) ? "user.email" : "user.name";
        var members = membershipRepository.findByOrganizationId(orgId, Sort.by(field));
        return members.stream().map(membershipMapper::toMemberDto).toList();
    }

    @Transactional
    public OrgMemberDto addMemberToOrganizationByEmail(UUID orgId, UUID callerId, boolean isAdmin, AddMemberToOrgRequest request) {
        if (!isAdmin) {
            requireOrgAdmin(orgId, callerId);
        } else if (!orgRepository.existsById(orgId)) {
            throw new OrganizationNotFoundException();
        }

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);
        var org = orgRepository.getReferenceById(orgId);
        if (membershipRepository.existsByOrganizationIdAndUserId(orgId, user.getId())) {
            throw new UserExistsInOrganizationException();
        }

        var orgMembership = OrgMembership.builder()
                        .orgRole(request.getOrgRole())
                        .user(user)
                        .organization(org)
                        .build();
        var saved = membershipRepository.save(orgMembership);
        return membershipMapper.toMemberDto(saved);
    }

    @Transactional
    public OrgMemberDto updateMemberRole(UUID orgId, UUID targetUserId, UUID callerId, boolean isAdmin, UpdateMemberRoleRequest request) {
        if (!isAdmin) {
            requireOrgAdmin(orgId, callerId);
        } else if (!orgRepository.existsById(orgId)) {
            throw new OrganizationNotFoundException();
        }

        var orgMembership = membershipRepository.findByOrganizationIdAndUserId(orgId, targetUserId).orElseThrow(MemberNotFoundException::new);

        if (orgMembership.getOrgRole() == OrgRole.ORG_ADMIN
                && request.getOrgRole() != OrgRole.ORG_ADMIN
                && membershipRepository.countByOrganizationIdAndOrgRole(orgId, OrgRole.ORG_ADMIN) <= 1) {
            throw new LastOrgAdminException();
        }

        orgMembership.setOrgRole(request.getOrgRole());
        var saved = membershipRepository.save(orgMembership);
        return membershipMapper.toMemberDto(saved);
    }

    private void requireOrgAdmin(UUID orgId, UUID userId) {
        var membership = membershipRepository.findByOrganizationIdAndUserId(orgId, userId).orElseThrow(OrganizationNotFoundException::new);
        if (membership.getOrgRole() != OrgRole.ORG_ADMIN) {
            throw new AccessDeniedException("Current user is not an ORG_ADMIN");
        }
    }

}
